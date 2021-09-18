package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SimpleBotAction;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;

@RequiredArgsConstructor
public class SimpleActionChecker implements BotActionChecker {

  private final SimpleBotAction<?> simpleBotAction;

  @Override
  public void check(BotAction<?> botAction) {
    Assert.assertNotNull(botAction);
    Assert.assertEquals(simpleBotAction, botAction);
  }
}
