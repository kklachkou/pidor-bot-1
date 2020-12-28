package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;

import java.util.Arrays;
import java.util.List;

public class RandomTextBotActionChecker implements BotActionChecker {
  private final List<String> options;

  public RandomTextBotActionChecker(String... options) {
    this.options = Arrays.asList(options);
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
    return options.stream().anyMatch(text::equals);
  }
}
