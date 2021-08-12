package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SendPhotoBotAction implements BotAction<Message> {

  private final long chatId;
  private final String photo;

  public SendPhotoBotAction(long chatId, String photo) {
    this.chatId = chatId;
    this.photo = photo;
  }

  @Override
  public Message process(Bot bot) throws TelegramApiException {
    SendPhoto sendPhoto =
        SendPhoto.builder()
            .chatId(String.valueOf(chatId))
            .photo(new InputFile(photo))
            .disableNotification(DateUtil.sleepTime())
            .build();
    return bot.execute(sendPhoto);
  }

  @Override
  public long getChatId() {
    return chatId;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + ": " + chatId + "-" + photo;
  }

  @Override
  public boolean hasLimit() {
    return true;
  }
}
