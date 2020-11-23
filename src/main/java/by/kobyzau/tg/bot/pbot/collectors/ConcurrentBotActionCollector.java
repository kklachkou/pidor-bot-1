package by.kobyzau.tg.bot.pbot.collectors;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.tasks.bot.SendMessagesToChatHandler;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.*;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Component
public class ConcurrentBotActionCollector implements BotActionCollector {

  private final Object lock = new Object();
  @Autowired private Bot bot;
  @Autowired private Logger logger;

  @Autowired
  @Qualifier("sendMessagesExecutor")
  private Executor executor;

  private final Map<Long, SendMessagesToChatHandler> handlers = new ConcurrentHashMap<>();

  @Override
  public void add(BotAction<?> botAction) {
    long chatId = botAction.getChatId();
    getHandler(chatId).add(botAction);
  }

  @Override
  public void collectHTMLMessage(long chatId, String message) {
    add(new SendMessageBotAction(chatId, message));
  }

  @Override
  public void wait(long chatId, int seconds, ChatAction chatAction) {
    add(new WaitBotAction(chatId, seconds, chatAction));
  }

  @Override
  public void wait(long chatId, ChatAction chatAction) {
    wait(chatId, CollectionUtil.getRandomValue(Arrays.asList(2, 3, 4, 5)), chatAction);
  }

  @Override
  public void typing(long chatId) {
    chatAction(chatId, ChatAction.TYPING);
  }

  @Override
  public void text(long chatId, Text text) {
    add(new SendMessageBotAction(chatId, text));
  }

  @Override
  public void text(long chatId, Text message, Integer replyToMessage) {
    add(new SendMessageBotAction(chatId, message, replyToMessage));
  }

  @Override
  public void chatAction(long chatId, ChatAction chatAction) {
    add(new ChatActionBotAction(chatId, chatAction));
  }

  @Override
  public void photo(long chatId, String photo) {
    add(new SendPhotoBotAction(chatId, photo));
  }

  @Override
  public void sticker(long chatId, String sticker) {
    add(new SendStickerBotAction(chatId, sticker));
  }

  @Override
  public void animation(long chatId, String fileId) {
    add(new SendAnimationBotAction(chatId, fileId));
  }

  private SendMessagesToChatHandler getHandler(long chatId) {
    synchronized (lock) {
      SendMessagesToChatHandler handler = handlers.get(chatId);
      if (handler == null || !handler.applyState(SendMessagesToChatHandler.BotHandlerState.WORKING)) {
        logger.debug("\uD83D\uDEE0 Creating new handler for chat " + chatId);
        handler = new SendMessagesToChatHandler(logger, bot, chatId);
        executor.execute(handler);
        handlers.put(chatId, handler);
      }
      return handler;
    }
  }
}
