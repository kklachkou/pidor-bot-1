package by.kobyzau.tg.bot.pbot.util;

import by.kobyzau.tg.bot.pbot.program.selection.PrioritySelection;

import java.util.Collection;

public class PriorityUtil {

  public static int getChance(
      PrioritySelection.Priority priority,
      Collection<PrioritySelection.Priority> allPriorities) {
    int total = allPriorities.stream().mapToInt(PrioritySelection.Priority::getCopies).sum();
    return 100 * priority.getCopies() / total;
  }
}
