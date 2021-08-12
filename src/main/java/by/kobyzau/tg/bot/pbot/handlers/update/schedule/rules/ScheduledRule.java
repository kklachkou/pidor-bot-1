package by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import java.time.LocalDate;

public interface ScheduledRule {

  boolean isMatch(long chatId, LocalDate localDate);

  ScheduledItem getItem();

  int SIMPLE_DAY_ORDER = -1000;
  int POTATO_ORDER = 0;
  int ELECTION_ORDER = 10;
  int EMOJI_ORDER = 40;
  int EXCLUDE_ORDER = 60;
}
