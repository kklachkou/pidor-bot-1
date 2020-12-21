package by.kobyzau.tg.bot.pbot.collectors;

import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.*;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;

import java.util.Arrays;

public abstract class AbstractBotActionCollector implements BotActionCollector {

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
}
