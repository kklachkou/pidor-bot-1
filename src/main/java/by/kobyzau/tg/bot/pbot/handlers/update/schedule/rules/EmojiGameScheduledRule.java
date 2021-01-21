package by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules.ScheduledRule.EMOJI_ORDER;

@Component
@Order(EMOJI_ORDER)
public class EmojiGameScheduledRule implements ScheduledRule {

  @Autowired private ChatSettingsService settingsService;

  private final Set<DayOfWeek> weekDays =
      new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.THURSDAY));

  @Override
  public boolean isMatch(long chatId, LocalDate localDate) {
    return weekDays.contains(localDate.getDayOfWeek())
        && (settingsService.isEnabled(
                ChatSettingsService.ChatCheckboxSettingType.GAMES_FREQUENT, chatId)
            || localDate.getDayOfYear() % 2 == 0);
  }

  @Override
  public ScheduledItem getItem() {
    return ScheduledItem.EMOJI_GAME;
  }
}
