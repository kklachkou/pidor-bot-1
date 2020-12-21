package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.tg.action.BotAction;

public class BotTypeBotActionChecker implements BotActionChecker {
  private final Class c;

  public BotTypeBotActionChecker(Class c) {
    this.c = c;
  }

  @Override
  public boolean check(BotAction<?> botAction) {
    return botAction != null && botAction.getClass().equals(c);
  }
}
