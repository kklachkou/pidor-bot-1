package by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
@Order(ScheduledRule.ELECTION_ORDER)
public class ElectionScheduledRule implements ScheduledRule {

  @Autowired private ChatSettingsService settingsService;

  @Override
  public boolean isMatch(long chatId, LocalDate localDate) {
    return localDate.getDayOfWeek() == DayOfWeek.SUNDAY
        && (settingsService.isEnabled(
                ChatSettingsService.ChatCheckboxSettingType.ELECTION_FREQUENT, chatId)
            || localDate.getDayOfYear() % 2 == 0);
  }

  @Override
  public ScheduledItem getItem() {
    return ScheduledItem.ELECTION;
  }
}
