package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SendMessageBotAction implements BotAction<Message> {

  private final long chatId;
  private final Text text;
  private final Integer replyToMessage;

  public SendMessageBotAction(long chatId, String message) {
    this(chatId, new SimpleText(message));
  }

  public SendMessageBotAction(long chatId, Text text) {
    this(chatId, text, null);
  }

  public SendMessageBotAction(long chatId, Text text, Integer replyToMessage) {
    this.chatId = chatId;
    this.text = text;
    this.replyToMessage = replyToMessage;
  }

  @Override
  public Message process(Bot bot) throws TelegramApiException {
    SendMessage sendMessage =
        SendMessage.builder()
            .chatId(String.valueOf(chatId))
            .text(text.text())
            .parseMode("html")
            .replyToMessageId(replyToMessage)
            .disableNotification(DateUtil.sleepTime())
            .build();
    return bot.execute(sendMessage);
  }

  @Override
  public long getChatId() {
    return chatId;
  }

  public Text getText() {
    return text;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + ": " + chatId + "-" + text;
  }
}
