package by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules.ScheduledRule.EXCLUDE_ORDER;

@Component
@Order(EXCLUDE_ORDER)
public class ExcludeGameScheduledRule implements ScheduledRule {

  private final Set<DayOfWeek> weekDays = new HashSet<>(Collections.singletonList(DayOfWeek.SATURDAY));

  @Override
  public boolean isMatch(long chatId, LocalDate localDate) {
    return weekDays.contains(localDate.getDayOfWeek());
  }

  @Override
  public ScheduledItem getItem() {
    return ScheduledItem.EXCLUDE_GAME;
  }
}
