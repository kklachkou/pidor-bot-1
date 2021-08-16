package by.kobyzau.tg.bot.pbot.collectors;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.tasks.bot.SendMessagesToChatHandler;
import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!integration-test")
public class ConcurrentBotActionCollector extends AbstractBotActionCollector {

  private final Object lock = new Object();
  @Autowired private Bot bot;
  @Autowired private Logger logger;

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
    synchronized (lock) {
      SendMessagesToChatHandler handler = handlers.get(chatId);
      if (handler == null
          || !handler.applyState(SendMessagesToChatHandler.BotHandlerState.WORKING)) {
        logger.debug("\uD83D\uDEE0 Creating new handler for chat " + chatId);
        handler = new SendMessagesToChatHandler(logger, bot, chatId, chatId == adminUserId);
        executor.execute(handler);
        handlers.put(chatId, handler);
      }
      return handler;
    }
  }
}
