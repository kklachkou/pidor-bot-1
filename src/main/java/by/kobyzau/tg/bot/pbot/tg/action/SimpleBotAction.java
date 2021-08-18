package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import java.io.Serializable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SimpleBotAction<T extends Serializable> implements BotAction<T> {
  private final long chatId;
  private final BotApiMethod<T> botApiMethod;
  private final boolean hasLimit;

  public SimpleBotAction(long chatId, BotApiMethod<T> botApiMethod) {
    this(chatId, botApiMethod, false);
  }

  public SimpleBotAction(long chatId, BotApiMethod<T> botApiMethod, boolean hasLimit) {
    this.botApiMethod = botApiMethod;
    this.chatId = chatId;
    this.hasLimit = hasLimit;
  }

  @Override
  public T process(Bot bot) throws TelegramApiException {
    return bot.execute(botApiMethod);
  }

  @Override
  public long getChatId() {
    return chatId;
  }

  @Override
  public boolean hasLimit() {
    return hasLimit;
  }
}
