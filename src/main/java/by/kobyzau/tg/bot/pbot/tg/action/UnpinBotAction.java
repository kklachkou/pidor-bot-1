package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class UnpinBotAction implements BotAction<Boolean> {

  private final long chatId;
  private final Integer messageId;

  public UnpinBotAction(long chatId, Integer messageId) {
    this.chatId = chatId;
    this.messageId = messageId;
  }

  @Override
  public Boolean process(Bot bot) throws TelegramApiException {
    return bot.execute(
        UnpinChatMessage.builder().chatId(String.valueOf(chatId)).messageId(messageId).build());
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
    return "UnpinBotAction{" + "chatId=" + chatId + ", messageId=" + messageId + '}';
  }
}
