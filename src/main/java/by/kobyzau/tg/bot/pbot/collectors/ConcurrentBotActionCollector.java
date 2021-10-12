package by.kobyzau.tg.bot.pbot.collectors;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.sender.RateChecker;
import by.kobyzau.tg.bot.pbot.tasks.bot.SendMessagesToChatHandler;
import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Profile("!integration-test")
public class ConcurrentBotActionCollector extends AbstractBotActionCollector {

  private final Lock lock = new ReentrantLock();
  @Autowired private Bot bot;
  @Autowired private Logger logger;
  @Autowired private RateChecker rateChecker;

  @Autowired
  @Qualifier("sendMessagesExecutor")
  private Executor executor;

  @Value("${app.admin.userId}")
  private long adminUserId;

  private final Map<Long, SendMessagesToChatHandler> handlers = new ConcurrentHashMap<>();

  @Override
  public void add(BotAction<?> botAction) {
    long chatId = botAction.getChatId();
    getHandler(chatId).add(botAction);
  }

  private SendMessagesToChatHandler getHandler(long chatId) {
    lock.lock();
    try {
      SendMessagesToChatHandler handler = handlers.get(chatId);
      if (handler == null
          || !handler.applyState(SendMessagesToChatHandler.BotHandlerState.WORKING)) {
        logger.debug("\uD83D\uDEE0 Creating new handler for chat " + chatId);
        handler = new SendMessagesToChatHandler(logger, bot, chatId, rateChecker);
        executor.execute(handler);
        handlers.put(chatId, handler);
      }
      return handler;
    } finally {
      lock.unlock();
    }
  }
}
