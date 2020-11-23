package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SendAnimationBotAction implements BotAction<Message> {

  private final long chatId;
  private final String docId;

  public SendAnimationBotAction(long chatId, String docId) {
    this.chatId = chatId;
    this.docId = docId;
  }

  @Override
  public Message process(Bot bot) throws TelegramApiException {
    SendAnimation sendAnimation =
        SendAnimation.builder()
            .chatId(String.valueOf(chatId))
            .animation(new InputFile(docId))
            .disableNotification(DateUtil.sleepTime())
            .build();
    return bot.execute(sendAnimation);
  }

  @Override
  public long getChatId() {
    return chatId;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + ": " + chatId + "-" + docId;
  }
}
