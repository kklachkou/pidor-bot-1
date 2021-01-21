package by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import org.springframework.core.annotation.Order;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem.EDITED_MESSAGE;
import static by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules.ScheduledRule.EDIT_MESSAGE_ORDER;

//@Component
@Order(EDIT_MESSAGE_ORDER)
public class EditMessageScheduledRule implements ScheduledRule {

  @Override
  public boolean isMatch(long chatId, LocalDate localDate) {
    return localDate.getDayOfWeek() == DayOfWeek.SUNDAY && localDate.getDayOfYear() % 2 == 0
        || localDate.isEqual(LocalDate.of(2020, 11, 4));
  }

  @Override
  public ScheduledItem getItem() {
    return EDITED_MESSAGE;
  }
}
