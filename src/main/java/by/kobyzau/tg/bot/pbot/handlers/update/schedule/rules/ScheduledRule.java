package by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import org.springframework.core.Ordered;

import java.time.LocalDate;

public interface ScheduledRule {

  boolean isMatch(long chatId, LocalDate localDate);

  ScheduledItem getItem();

  int SIMPLE_DAY_ORDER = Ordered.HIGHEST_PRECEDENCE;
  int ELECTION_ORDER = 0;
  int EMOJI_ORDER = 40;
  int EXCLUDE_ORDER = 60;
}
