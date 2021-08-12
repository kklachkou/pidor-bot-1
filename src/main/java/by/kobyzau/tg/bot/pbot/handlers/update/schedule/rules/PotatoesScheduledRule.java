package by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import java.time.DayOfWeek;
import java.time.LocalDate;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules.ScheduledRule.POTATO_ORDER;

@Component
@Order(POTATO_ORDER)
public class PotatoesScheduledRule implements ScheduledRule {

  @Override
  public boolean isMatch(long chatId, LocalDate localDate) {
    return localDate.getDayOfWeek() == DayOfWeek.SATURDAY && localDate.getDayOfYear() % 2 == 0;
  }

  @Override
  public ScheduledItem getItem() {
    return ScheduledItem.POTATOES;
  }
}
