package by.kobyzau.tg.bot.pbot.tasks.bot;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class SendMessagesToChatHandler implements Runnable {

  private final Lock lock = new ReentrantLock();
  private static final int THREAD_LIVE_IN_PENDING = 15;
  private static final int LIMIT_PER_MINUTE = 20;
  private final AtomicInteger numIterationsWithPending = new AtomicInteger(0);
  private volatile BotHandlerState state;
  private final Logger logger;
  private final Bot bot;
  private final long chatId;
  private final Queue<BotAction<?>> queue = new ConcurrentLinkedQueue<>();
  private final Queue<Long> executionTime = new ConcurrentLinkedQueue<>();

  public SendMessagesToChatHandler(Logger logger, Bot bot, long chatId) {
    this.logger = logger;
    this.bot = bot;
    this.chatId = chatId;
    this.state = BotHandlerState.PENDING;
  }

  @Override
  public void run() {
    while (this.state != BotHandlerState.DEAD) {
      BotAction<?> botAction = queue.poll();
      while (botAction != null) {
        applyState(BotHandlerState.WORKING);
        send(botAction);
        botAction = queue.poll();
      }
      applyState(BotHandlerState.PENDING);
      if (applyState(BotHandlerState.DEAD)) {
        logger.debug("\uD83D\uDEE0 Killing Chat Handler for chat " + chatId);
      }
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        logger.error("Cannot sleep in sendMessagesToChat", e);
      }
    }
  }

  private void send(BotAction<?> botAction) {
    if (botAction.hasLimit()) {
      while (isExceedTimeLimit()) {
        try {
          Thread.sleep(300);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    executionTime.add(System.currentTimeMillis());
    try {
      logger.debug("✉️ Sending new bot action:\n\n<pre>" + botAction + "</pre>");
      botAction.process(bot);
    } catch (TelegramApiRequestException tgEx) {
      logger.error("Cannot send message:\n\n " + botAction + "\n\n" + tgEx.getApiResponse(), tgEx);
    } catch (Exception e) {
      logger.error("Cannot send message:\n\n " + botAction, e);
    }
  }

  public void add(BotAction<?> botAction) {
    queue.add(botAction);
  }

  public long getChatId() {
    return chatId;
  }

  public BotHandlerState getState() {
    return state;
  }

  public boolean applyState(BotHandlerState state) {
    try {
      lock.lock();
      switch (this.state) {
        case DEAD:
          return state == BotHandlerState.DEAD;
        case WORKING:
          switch (state) {
            case WORKING:
              this.numIterationsWithPending.set(0);
              return true;
            case PENDING:
              this.numIterationsWithPending.set(0);
              this.state = BotHandlerState.PENDING;
              return true;
            case DEAD:
              return false;
          }
        case PENDING:
          switch (state) {
            case WORKING:
              this.numIterationsWithPending.set(0);
              this.state = BotHandlerState.WORKING;
              return true;
            case DEAD:
              if (numIterationsWithPending.get() > THREAD_LIVE_IN_PENDING * 2) {
                this.state = BotHandlerState.DEAD;
                return true;
              }
              return false;
            case PENDING:
              if (this.numIterationsWithPending.get() < 0) {
                this.numIterationsWithPending.set(0);
              }
              this.numIterationsWithPending.incrementAndGet();
              return true;
          }
      }
    } finally {
      lock.unlock();
    }

    return false;
  }

  private boolean isExceedTimeLimit() {
    long currentTime = System.currentTimeMillis();
    boolean inLastSecond = executionTime.stream().anyMatch(l -> l > (currentTime - 600));
    if (inLastSecond) {
      return true;
    }
    return executionTime.stream().filter(l -> l > (currentTime - 1010 * 60)).count()
        >= LIMIT_PER_MINUTE;
  }

  public enum BotHandlerState {
    PENDING,
    WORKING,
    DEAD
  }
}
