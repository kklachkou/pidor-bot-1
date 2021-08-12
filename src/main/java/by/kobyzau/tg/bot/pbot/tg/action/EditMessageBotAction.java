package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class EditMessageBotAction implements BotAction<Message> {

  private final Message message;
  private final Text text;
  private final InlineKeyboardMarkup replyKeyboard;

  public EditMessageBotAction(Message message, Text text, InlineKeyboardMarkup replyKeyboard) {
    this.message = message;
    this.text = text;
    this.replyKeyboard = replyKeyboard;
  }

  public EditMessageBotAction(Message message, Text text) {
    this(message, text, null);
  }

  @Override
  public Message process(Bot bot) throws TelegramApiException {
    EditMessageText message =
        EditMessageText.builder()
            .chatId(String.valueOf(getChatId()))
            .messageId(this.message.getMessageId())
            .parseMode("html")
            .replyMarkup(replyKeyboard)
            .text(text.text())
            .build();
    return (Message) bot.execute(message);
  }

  @Override
  public long getChatId() {
    return message.getChatId();
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()
        + ": "
        + getChatId()
        + " - "
        + text
        + " - reply:"
        + (replyKeyboard != null);
  }

  @Override
  public boolean hasLimit() {
    return true;
  }
}
