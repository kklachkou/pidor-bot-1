package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.tg.action.BotAction;

public class AnyBotActionChecker implements BotActionChecker {

  @Override
  public boolean check(BotAction<?> botAction) {
    return botAction != null;
  }
}
