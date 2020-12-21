package by.kobyzau.tg.bot.pbot.handlers.command.handler.schedule;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameType;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.program.text.ItalicText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.service.DiceService;
import by.kobyzau.tg.bot.pbot.service.ExcludeGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class CalendarScheduleDayTextItem implements ScheduleDayTextItem {

  @Autowired private CalendarSchedule calendarSchedule;
  @Autowired private DiceService diceService;
  @Autowired private ExcludeGameService excludeGameService;

  @Override
  public Optional<Text> getTextItem(LocalDate localDate) {
    ScheduledItem item = calendarSchedule.getItem(localDate);
    switch (item) {
      case EMOJI_GAME:
        return diceService
            .getGame(localDate)
            .map(EmojiGame::getType)
            .map(EmojiGameType::getGameName)
            .map(SimpleText::new);
      case EXCLUDE_GAME:
        return Optional.of(
            new ParametizedText(
                "Игра {0}", new ItalicText(excludeGameService.getWordOfTheDay(localDate))));
      case EDITED_MESSAGE:
        return Optional.of(new SimpleText("Проверка на внимательность"));
      case ELECTION:
        return Optional.of(new SimpleText("Пидор-выборы"));
      default:
        return Optional.empty();
    }
  }
}
