package by.kobyzau.tg.bot.pbot.bots.game.emoji;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameResult;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameType;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.PidorDice;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.DiceService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BowlingEmojiGame implements EmojiGame {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private DiceService diceService;

  @Override
  public EmojiGameType getType() {
    return EmojiGameType.BOWLING;
  }

  @Override
  public boolean isDateToGame(LocalDate date) {
    return date.getDayOfYear() % 7 == 0;
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
    if (userValue == 0) {
      return EmojiGameResult.NONE;
    }
    return EmojiGameResult.LOSE;
  }

  @Override
  public void printIntro(long chatId) {
    int numPidorsToPlay = diceService.getNumPidorsToPlay(chatId);
    botActionCollector.text(
        chatId,
        new RandomText(
            "Сегодня будем гонять шары!",
            "Сегодня играем в боулинг!",
            "Сегодня будем играть в боулинг!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new SimpleText("Выбей больше остальных - и ты не будешь пидором!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new SimpleText("Пидором будет один из тех, кто выбил меньше всего кеглей"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new SimpleText("Если не бросишь шар - твои шансы стать пидором удвоятся!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "Я засчитываю только первые {0} бросков! Поторопись!", new IntText(numPidorsToPlay)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Кидаю шар!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
  }
}
