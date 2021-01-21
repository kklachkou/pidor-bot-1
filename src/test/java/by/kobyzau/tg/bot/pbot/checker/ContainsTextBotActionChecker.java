package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

public class ContainsTextBotActionChecker implements BotActionChecker {
  private final List<String> searchText;

  public ContainsTextBotActionChecker(String... searchText) {
    this(Arrays.asList(searchText));
  }

  public ContainsTextBotActionChecker(List<String> searchText) {
    this.searchText = searchText;
  }

  @Override
  public void check(BotAction<?> botAction) {
    Assert.assertNotNull(botAction);
    if (botAction instanceof SendMessageBotAction) {
      check((SendMessageBotAction) botAction);
    } else {
      Assert.fail("Bot action is not Send Message: " + botAction);
    }
  }

  private void check(SendMessageBotAction botAction) {
    String text = botAction.getText().text();
    Assert.assertNotNull(text);
    for (String s : searchText) {
      if (text.contains(s)) {
        return;
      }
    }
    Assert.fail(
        "No matched text with bot action\nThe Bot Action "
            + botAction
            + "\nshould contains at least one the following text:\n"
            + String.join(",", searchText));
  }
}
