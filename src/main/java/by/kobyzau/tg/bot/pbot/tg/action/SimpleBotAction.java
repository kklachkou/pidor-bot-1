package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import java.io.Serializable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SimpleBotAction<T extends Serializable> implements BotAction<T> {
  private final BotApiMethod<T> botApiMethod;

  public SimpleBotAction(BotApiMethod<T> botApiMethod) {
    this.botApiMethod = botApiMethod;
  }

  @Override
  public T process(Bot bot) throws TelegramApiException {
    return bot.execute(botApiMethod);
  }

  @Override
  public long getChatId() {
    return 0;
  }

  @Override
  public boolean hasLimit() {
    return true;
  }
}
