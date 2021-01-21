package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import org.junit.Assert;

public class BotTypeBotActionChecker implements BotActionChecker {
  private final Class<? extends BotAction<?>> c;

  public BotTypeBotActionChecker(Class<? extends BotAction<?>> c) {
    this.c = c;
  }

  @Override
  public void check(BotAction<?> botAction) {
    Assert.assertNotNull(botAction);
    Assert.assertEquals(c, botAction.getClass());
  }
}
