package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class SimpleStickerBotAction implements BotAction<Message> {
  private final long chatId;
  private final SendSticker sendSticker;

  @Override
  public Message process(Bot bot) throws TelegramApiException {
    return bot.execute(sendSticker);
  }

  @Override
  public long getChatId() {
    return chatId;
  }

  @Override
  public boolean hasLimit() {
    return true;
  }

  @Override
  public String toString() {
    return "SimpleStickerBotAction{"
        + "chatId="
        + chatId
        + ", sendSticker="
        + sendSticker.getSticker().getAttachName()
        + '}';
  }
}
