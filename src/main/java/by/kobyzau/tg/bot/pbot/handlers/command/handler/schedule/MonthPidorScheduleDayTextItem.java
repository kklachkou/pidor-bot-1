package by.kobyzau.tg.bot.pbot.handlers.command.handler.schedule;

import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.PastMonth;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class MonthPidorScheduleDayTextItem implements ScheduleDayTextItem {

  @Override
  public Optional<Text> getTextItem(LocalDate localDate) {
    if (localDate.getDayOfMonth() == 1) {
      return Optional.of(
          new ParametizedText(
              "Объявление главного пидора {0}",
              new PastMonth(localDate.minusDays(1).getMonthValue())));
    }
    return Optional.empty();
  }
}
