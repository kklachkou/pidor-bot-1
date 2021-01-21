package by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules;

import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased.DateBasePidorFunnyAction;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules.ScheduledRule.SIMPLE_DAY_ORDER;

@Component
@Order(SIMPLE_DAY_ORDER)
public class SimpleDaysScheduledRule implements ScheduledRule {

  @Autowired private List<DateBasePidorFunnyAction> dateBasePidorFunnyActions;

  @Override
  public boolean isMatch(long chatId, LocalDate localDate) {
    return isNewYear(localDate)
        || isDateBasedAction(localDate)
        || isBirthday(localDate);
  }

  private boolean isNewYear(LocalDate date) {
    return (date.getMonth() == Month.JANUARY && date.getDayOfYear() == 1)
        || (date.getMonth() == Month.DECEMBER && date.plusDays(1).getDayOfYear() == 1);
  }

  private boolean isDateBasedAction(LocalDate date) {
    return dateBasePidorFunnyActions.stream().anyMatch(d -> d.testDate(date));
  }

  private boolean isBirthday(LocalDate date) {
    return (date.getMonth() == Month.DECEMBER && date.getDayOfMonth() == 5)
        || (date.getMonth() == Month.DECEMBER && date.getDayOfMonth() == 25);
  }

  @Override
  public ScheduledItem getItem() {
    return ScheduledItem.NONE;
  }
}
