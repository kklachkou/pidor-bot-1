package by.kobyzau.tg.bot.pbot.sender.async;

import by.kobyzau.tg.bot.pbot.bots.PidorBot;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.sender.BotApiMethodCallback;
import by.kobyzau.tg.bot.pbot.sender.BotSender;
import by.kobyzau.tg.bot.pbot.sender.async.runner.AsyncGroupSenderRunner;
import by.kobyzau.tg.bot.pbot.sender.methods.SendMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static by.kobyzau.tg.bot.pbot.sender.async.runner.SenderRunner.RunnerState.WORKING;

@Component("pidorBotGroupSender")
public class AsyncPidorBotGroupSender implements BotSender {

  private final Lock lock = new ReentrantLock();

  @Autowired private PidorBot bot;
  @Autowired private Logger logger;

  @Autowired
  @Qualifier("asyncPidorBotSenderExecutor")
  private Executor executor;

  private final Map<Long, AsyncGroupSenderRunner> runners = new ConcurrentHashMap<>();

  @Override
  public void send(long chatId, SendMethod<?> method) {
    getRunner(chatId).add(new AsyncGroupSenderRunner.Param<>(method));
  }

  @Override
  public <T> void send(long chatId, SendMethod<T> method, BotApiMethodCallback<T> callback) {
    getRunner(chatId).add(new AsyncGroupSenderRunner.Param<>(method, callback));
  }

  private AsyncGroupSenderRunner getRunner(long chatId) {
    lock.lock();
    try {
      AsyncGroupSenderRunner runner = runners.get(chatId);
      if (runner == null || !runner.applyState(WORKING)) {
        runner = new AsyncGroupSenderRunner(bot, logger);
        executor.execute(runner);
        runners.put(chatId, runner);
      }
      return runner;
    } finally {
      lock.unlock();
    }
  }
}
