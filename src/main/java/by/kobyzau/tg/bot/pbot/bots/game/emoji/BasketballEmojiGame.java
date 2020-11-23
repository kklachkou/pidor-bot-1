package by.kobyzau.tg.bot.pbot.bots.game.emoji;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameResult;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameType;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.DiceService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BasketballEmojiGame implements EmojiGame {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired
  private DiceService diceService;

  @Override
  public String getEmoji() {
    return "\ud83c\udfc0";
  }

  @Override
  public EmojiGameType getType() {
    return EmojiGameType.BASKETBALL;
  }

  @Override
  public boolean isDateToGame(LocalDate date) {
    switch (date.getDayOfWeek()) {
      case THURSDAY:
        return date.getDayOfMonth() % 2 != 0;
      case TUESDAY:
        return true;
      default:
        return false;
    }
  }

  @Override
  public EmojiGameResult getResult(long chatId, int userValue) {
    if (userValue == 0) {
      return EmojiGameResult.NONE;
    }
    if (userValue > 3) {
      return EmojiGameResult.WIN;
    }
    return EmojiGameResult.LOSE;
  }

  @Override
  public void printIntro(long chatId) {
    int numPidorsToPlay = diceService.getNumPidorsToPlay(chatId);
    botActionCollector.text(chatId, new SimpleText("Сегодня баскетбол!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new SimpleText("Все, кто закинет мяч в кольцо - не станет пидором дня"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new SimpleText(
            "Если не сможешь закинуть 3-х <b>очко</b><i>вый</i> - то ты участвуешь в вечерней рулетке"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new SimpleText("Однако если вы все закините мяч - от меня вам всем не спастись:)"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
            chatId, new SimpleText("Если не бросишь мяч - твои шансы стать пидором удвоятся!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
            chatId,
            new ParametizedText(
                    "Я засчитываю только первые {0} бросков! Поторопись!", new IntText(numPidorsToPlay)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Делай как я!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
  }
}
