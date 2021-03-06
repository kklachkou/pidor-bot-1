package by.kobyzau.tg.bot.pbot.tasks.bot;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.sender.RateChecker;
import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import by.kobyzau.tg.bot.pbot.util.ThreadUtil;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SendMessagesToChatHandler implements Runnable {

  private final Lock lock = new ReentrantLock();
  private static final int THREAD_LIVE_IN_PENDING = 3;
  private final AtomicInteger numIterationsWithPending = new AtomicInteger(0);
  private volatile BotHandlerState state;
  private final Logger logger;
  private final Bot bot;
  private final long chatId;
  private final RateChecker rateChecker;
  private final boolean isPrivateChat;
  private final Queue<BotAction<?>> queue = new ConcurrentLinkedQueue<>();

  public SendMessagesToChatHandler(Logger logger, Bot bot, long chatId, RateChecker rateChecker, boolean isPrivateChat) {
    this.logger = logger;
    this.bot = bot;
    this.chatId = chatId;
    this.state = BotHandlerState.PENDING;
    this.rateChecker = rateChecker;
    this.isPrivateChat = isPrivateChat;
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
      ThreadUtil.sleep(500);
    }
  }

  private void send(BotAction<?> botAction) {
    if (botAction.hasLimit()) {
      while (!canSendMessage()) {
        ThreadUtil.sleep(300);
      }
    }
    try {
      logger.debug("?????? Sending new bot action:\n\n<pre>" + botAction + "</pre>");
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
              if (numIterationsWithPending.get() > THREAD_LIVE_IN_PENDING) {
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

  private boolean canSendMessage() {
    return rateChecker.canSendMessage(chatId);
  }

  public enum BotHandlerState {
    PENDING,
    WORKING,
    DEAD
  }
}
