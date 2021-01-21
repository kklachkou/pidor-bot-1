package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import org.junit.Assert;

public class AnyBotActionChecker implements BotActionChecker {

  @Override
  public void check(BotAction<?> botAction) {
    Assert.assertNotNull(botAction);
  }
}
