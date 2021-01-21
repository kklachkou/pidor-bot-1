package by.kobyzau.tg.bot.pbot.handlers.update.schedule;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules.ScheduledRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CalendarSchedule {

  @Autowired private List<ScheduledRule> rules;

  public ScheduledItem getItem(long chatId, LocalDate date) {
    return rules.stream()
            .filter(r -> r.isMatch(chatId, date))
            .findFirst()
            .map(ScheduledRule::getItem)
            .orElse(ScheduledItem.NONE);
  }
}
