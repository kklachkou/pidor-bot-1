package by.kobyzau.tg.bot.pbot.handlers.command.handler.schedule;

import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class BlackBoxScheduleDayTextItem implements ScheduleDayTextItem {
  @Override
  public Optional<Text> getTextItem(long chatId, LocalDate localDate) {
    if (localDate.getDayOfWeek() == DayOfWeek.SUNDAY
        || localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
      return Optional.of(new SimpleText("Черный ящик с артефактом"));
    }
    return Optional.empty();
  }
}
