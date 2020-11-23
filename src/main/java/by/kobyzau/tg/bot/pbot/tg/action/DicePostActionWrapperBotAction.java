package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import org.telegram.telegrambots.meta.api.objects.Dice;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;
import java.util.function.Consumer;

public class DicePostActionWrapperBotAction implements BotAction<Message> {

  private final BotAction<Message> diceAction;

  private final Consumer<Integer> dicePostAction;

  public DicePostActionWrapperBotAction(
          long chatId, EmojiGame game, Consumer<Integer> dicePostAction) {
    this.diceAction = new DiceBotAction(chatId, game);
    this.dicePostAction = dicePostAction;
  }

  @Override
  public Message process(Bot bot) throws TelegramApiException {
    Message message = diceAction.process(bot);
    Optional.ofNullable(message.getDice()).map(Dice::getValue).ifPresent(dicePostAction);
    return message;
  }

  @Override
  public long getChatId() {
    return diceAction.getChatId();
  }
}
