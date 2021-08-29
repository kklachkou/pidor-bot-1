package by.kobyzau.tg.bot.pbot.sender.async.runner;

import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.sender.BotApiMethodCallback;
import by.kobyzau.tg.bot.pbot.sender.methods.SendMethod;
import by.kobyzau.tg.bot.pbot.util.ThreadUtil;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("BusyWait")
public class AsyncGroupSenderRunner extends DefaultSenderRunner {

  private final Set<String> limitedMethods;
  private static final int LIMIT_PER_MINUTE = 20;

  private final Queue<Long> executionTime = new ConcurrentLinkedQueue<>();
  private final Queue<Param<?>> queue = new ConcurrentLinkedQueue<>();

  private final AbsSender sender;
  private final Logger logger;

  public AsyncGroupSenderRunner(AbsSender sender, Logger logger) {
    this.sender = sender;
    this.logger = logger;
    this.limitedMethods =
        Collections.unmodifiableSet(
            new HashSet<>(
                Arrays.asList(
                    "sendmessage",
                    "copyMessage",
                    "sendPhoto",
                    "sendMediaGroup",
                    "sendVideo",
                    "deleteMessage",
                    "editmessagereplymarkup",
                    "editmessagetext",
                    "forwardmessage",
                    "sendSticker")));
  }

  @Override
  public void add(Param<?> botAction) {
    queue.add(botAction);
  }

  @Override
  public void run() {
    while (getState() != RunnerState.DEAD) {
      Param<?> param = queue.poll();
      while (param != null) {
        applyState(RunnerState.WORKING);
        send(param);
        param = queue.poll();
      }
      applyState(RunnerState.PENDING);
      applyState(RunnerState.DEAD);
      ThreadUtil.sleep(500);
    }
  }

  private <T> void send(Param<T> param) {
    try {
      boolean limitedMethod = limitedMethods.contains(param.getMethod().getMethod());
      while (limitedMethod && isExceedTimeLimit()) {
        ThreadUtil.sleep(300);
      }
      SendMethod<T> method = param.getMethod();
      BotApiMethodCallback<T> callback = param.getCallback();
      T result = method.send(sender);
      if (limitedMethod) {
        executionTime.add(System.currentTimeMillis());
      }
      if (callback != null) {
        callback.handleResult(result);
      }
    } catch (Exception e) {
      logger.error("Cannot send message: " + e.getMessage(), e);
    }
  }

  private boolean isExceedTimeLimit() {
    long currentTime = System.currentTimeMillis();
    boolean inLastSecond = executionTime.stream().anyMatch(l -> l > (currentTime - 600));
    if (inLastSecond) {
      return true;
    }
    return executionTime.stream().filter(l -> l > (currentTime - 1000 * 60)).count()
        >= LIMIT_PER_MINUTE;
  }
}
