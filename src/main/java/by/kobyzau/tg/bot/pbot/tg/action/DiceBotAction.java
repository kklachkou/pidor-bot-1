package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameType;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DiceBotAction implements BotAction<Message> {

  private final long chatId;
  private final EmojiGameType emojiGameType;

  public DiceBotAction(long chatId, EmojiGameType emojiGameType) {
    this.chatId = chatId;
    this.emojiGameType = emojiGameType;
  }

  @Override
  public Message process(Bot bot) throws TelegramApiException {
    SendDice sendDice =
        SendDice.builder()
            .emoji(emojiGameType.getEmoji())
            .chatId(String.valueOf(chatId))
            .disableNotification(DateUtil.sleepTime())
            .build();
    return bot.execute(sendDice);
  }

  @Override
  public long getChatId() {
    return chatId;
  }

  @Override
  public String toString() {
    return "DiceBotAction{" + "chatId=" + chatId + ", emoji=" + emojiGameType.name() + '}';
  }
}
