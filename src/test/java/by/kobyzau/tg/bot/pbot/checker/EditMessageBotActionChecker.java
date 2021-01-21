package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageBotAction;
import org.junit.Assert;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class EditMessageBotActionChecker implements BotActionChecker {

  private final Message message;
  private final Text text;
  private final InlineKeyboardMarkup replyKeyboard;

  public EditMessageBotActionChecker(
      Message message, Text text, InlineKeyboardMarkup replyKeyboard) {
    this.message = message;
    this.text = text;
    this.replyKeyboard = replyKeyboard;
  }

  @Override
  public void check(BotAction<?> botAction) {
    Assert.assertNotNull(botAction);
    Assert.assertEquals(
        new EditMessageBotAction(message, text, replyKeyboard).toString(), botAction.toString());
  }
}
