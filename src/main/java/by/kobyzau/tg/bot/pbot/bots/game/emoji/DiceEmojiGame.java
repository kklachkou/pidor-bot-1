package by.kobyzau.tg.bot.pbot.bots.game.emoji;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameResult;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameType;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.PidorDice;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.DiceService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DiceEmojiGame implements EmojiGame {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private DiceService diceService;

  @Override
  public String getEmoji() {
    return "\ud83c\udfb2";
  }

  @Override
  public EmojiGameType getType() {
    return EmojiGameType.DICE;
  }

  @Override
  public boolean isDateToGame(LocalDate date) {
    switch (date.getDayOfWeek()) {
      case MONDAY:
        return date.getDayOfMonth() % 2 == 0;
      case WEDNESDAY:
      case FRIDAY:
        return true;
      default:
        return false;
    }
  }

  @Override
  public EmojiGameResult getResult(long chatId, int userValue) {
    int minValue =
        diceService.getDices(chatId, DateUtil.now()).stream()
            .mapToInt(PidorDice::getValue)
            .min()
            .orElse(0);
    if (userValue > minValue) {
      return EmojiGameResult.WIN;
    }
    if (userValue == minValue) {
      return EmojiGameResult.LOSE;
    }
    if (userValue == 0) {
      return EmojiGameResult.NONE;
    }

    return EmojiGameResult.LOSE;
  }

  @Override
  public void printIntro(long chatId) {
    int numPidorsToPlay = diceService.getNumPidorsToPlay(chatId);
    botActionCollector.text(chatId, new SimpleText("Сегодня буду бросать кости!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new SimpleText("Выкинь больше остальных - и ты не будешь пидором!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new SimpleText("Пидором будет один из тех, кто выкинул наименьшее число"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new SimpleText("Если не бросишь кубик - твои шансы стать пидором удвоятся!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "Я засчитываю только первые {0} бросков! Поторопись!", new IntText(numPidorsToPlay)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Кидаю кость!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
  }
}
