package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SendStickerBotAction implements BotAction<Message> {

  private final long chatId;
  private final String stickerId;

  public SendStickerBotAction(long chatId, String stickerId) {
    this.chatId = chatId;
    this.stickerId = stickerId;
  }

  @Override
  public Message process(Bot bot) throws TelegramApiException {
    SendSticker sendSticker =
        SendSticker.builder()
            .chatId(String.valueOf(chatId))
            .sticker(new InputFile(stickerId))
            .disableNotification(DateUtil.sleepTime())
            .build();
    return bot.execute(sendSticker);
  }

  @Override
  public long getChatId() {
    return chatId;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + ": " + chatId + "-" + stickerId;
  }
}
