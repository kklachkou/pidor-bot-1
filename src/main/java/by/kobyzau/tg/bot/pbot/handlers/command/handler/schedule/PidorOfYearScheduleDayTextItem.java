package by.kobyzau.tg.bot.pbot.handlers.command.handler.schedule;

import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class PidorOfYearScheduleDayTextItem implements ScheduleDayTextItem {

  @Override
  public Optional<Text> getTextItem(LocalDate localDate) {
    LocalDate nextDay = localDate.plusDays(1);
    if (nextDay.getDayOfYear() == 1) {
      return Optional.of(
          new ParametizedText(
              "Объявление главного пидора {0} года", new IntText(localDate.getYear())));
    }
    return Optional.empty();
  }
}
