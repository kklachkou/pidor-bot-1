package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ReplyKeyboardBotAction implements BotAction<Message> {
  private final long chatId;
  private final Text text;
  private final Integer replyMessageId;
  private final ReplyKeyboard replyKeyboard;

  public ReplyKeyboardBotAction(
      long chatId, Text text, ReplyKeyboard replyKeyboard, Integer replyMessageId) {
    this.chatId = chatId;
    this.text = text;
    this.replyKeyboard = replyKeyboard;
    this.replyMessageId = replyMessageId;
  }

  @Override
  public Message process(Bot bot) throws TelegramApiException {
    SendMessage sendMessage =
        SendMessage.builder()
            .chatId(String.valueOf(chatId))
            .text(text.text())
            .parseMode("html")
            .disableNotification(DateUtil.sleepTime())
            .replyMarkup(replyKeyboard)
            .replyToMessageId(replyMessageId)
            .build();
    return bot.execute(sendMessage);
  }

  @Override
  public boolean hasLimit() {
    return true;
  }

  @Override
  public long getChatId() {
    return chatId;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + ": " + chatId + "-" + text;
  }
}
