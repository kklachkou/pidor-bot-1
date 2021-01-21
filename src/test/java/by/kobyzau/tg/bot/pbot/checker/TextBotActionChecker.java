package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import org.junit.Assert;

public class TextBotActionChecker implements BotActionChecker {

  private final long chatId;
  private final Text text;

  public TextBotActionChecker(long chatId, Text text) {
    this.chatId = chatId;
    this.text = text;
  }

  @Override
  public void check(BotAction<?> botAction) {
    Assert.assertNotNull(botAction);
    Assert.assertEquals(
        SendMessageBotAction.class.getSimpleName() + ": " + chatId + "-" + text,
        botAction.toString());
  }
}
