package by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules.ScheduledRule.ASSASSIN_ORDER;

@Component
@Order(ASSASSIN_ORDER)
public class AssassinScheduledRule implements ScheduledRule {

  @Override
  public boolean isMatch(LocalDate localDate) {
    return localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY || localDate.getDayOfWeek() == DayOfWeek.SUNDAY;
  }

  @Override
  public ScheduledItem getItem() {
    return ScheduledItem.ASSASSIN;
  }
}
