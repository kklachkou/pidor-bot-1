package by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
@Order(ScheduledRule.ELECTION_ORDER)
public class ElectionScheduledRule implements ScheduledRule {

  @Override
  public boolean isMatch(LocalDate localDate) {
    return localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY
        || localDate.getDayOfYear() % 13 == 0
        || localDate.isEqual(LocalDate.of(2020, 12, 18));
  }

  @Override
  public ScheduledItem getItem() {
    return ScheduledItem.ELECTION;
  }
}
