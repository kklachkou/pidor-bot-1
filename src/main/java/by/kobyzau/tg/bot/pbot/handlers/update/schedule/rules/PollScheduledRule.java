package by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules.ScheduledRule.POLL_ORDER;

@Component
@Order(POLL_ORDER)
public class PollScheduledRule implements ScheduledRule {

  @Override
  public boolean isMatch(LocalDate localDate) {
    return localDate.getDayOfYear() % 13 == 0;
  }

  @Override
  public ScheduledItem getItem() {
    return ScheduledItem.POLL;
  }
}
