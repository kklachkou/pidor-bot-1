package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SetMyCommandBotAction implements BotAction<Boolean> {

  private final SetMyCommands setMyCommands;

  public SetMyCommandBotAction(SetMyCommands setMyCommands) {
    this.setMyCommands = setMyCommands;
  }

  @Override
  public Boolean process(Bot bot) throws TelegramApiException {
    return bot.execute(setMyCommands);
  }

  @Override
  public long getChatId() {
    return 0;
  }

  @Override
  public String toString() {
    return "SetMyCommandBotAction{" +
            "setMyCommands=" + setMyCommands +
            '}';
  }
}
