package by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
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

  private final Set<DayOfWeek> weekDays =
      new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.THURSDAY));

  @Override
  public boolean isMatch(LocalDate localDate) {
    return weekDays.contains(localDate.getDayOfWeek());
  }

  @Override
  public ScheduledItem getItem() {
    return ScheduledItem.EMOJI_GAME;
  }
}
