package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;

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
  public boolean check(BotAction<?> botAction) {
    if (botAction == null) {
      return false;
    }
    if (botAction instanceof SendMessageBotAction) {
      return check((SendMessageBotAction) botAction);
    }

    return false;
  }

  private boolean check(SendMessageBotAction botAction) {
    String text = botAction.getText().text();
    if (text == null) {
      return false;
    }
    for (String s : searchText) {
      if (text.contains(s)) {
        return true;
      }
    }
    return false;
  }
}
