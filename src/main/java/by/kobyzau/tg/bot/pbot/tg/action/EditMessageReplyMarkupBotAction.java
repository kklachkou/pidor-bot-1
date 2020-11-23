package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class EditMessageReplyMarkupBotAction implements BotAction<Boolean> {

  private final long chatId;
  private final int messageId;
  private final InlineKeyboardMarkup replyKeyboard;

  public EditMessageReplyMarkupBotAction(
      long chatId, int messageId, InlineKeyboardMarkup replyKeyboard) {
    this.chatId = chatId;
    this.messageId = messageId;
    this.replyKeyboard = replyKeyboard;
  }

  @Override
  public Boolean process(Bot bot) throws TelegramApiException {
    bot.execute(
        EditMessageReplyMarkup.builder()
            .messageId(messageId)
            .chatId(String.valueOf(chatId))
            .replyMarkup(replyKeyboard)
            .build());
    return true;
  }

  @Override
  public long getChatId() {
    return chatId;
  }
}
