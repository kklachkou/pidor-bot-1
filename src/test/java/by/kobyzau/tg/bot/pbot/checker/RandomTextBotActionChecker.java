package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

public class RandomTextBotActionChecker implements BotActionChecker {
  private final List<String> options;

  public RandomTextBotActionChecker(String... options) {
    this.options = Arrays.asList(options);
  }

  @Override
  public void check(BotAction<?> botAction) {
    Assert.assertNotNull(botAction);
    if (botAction instanceof SendMessageBotAction) {
      check((SendMessageBotAction) botAction);
    } else {
      Assert.fail("Bot action is not send message: " + botAction);
    }
  }

  private void check(SendMessageBotAction botAction) {
    String text = botAction.getText().text();
    Assert.assertNotNull(text);
    if (options.stream().noneMatch(text::equals)) {
      Assert.fail("No text match: " + botAction);
    }
  }
}
