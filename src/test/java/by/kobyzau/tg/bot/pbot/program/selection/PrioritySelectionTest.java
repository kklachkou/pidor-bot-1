package by.kobyzau.tg.bot.pbot.program.selection;

import by.kobyzau.tg.bot.pbot.RangeMatcher;
import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.util.PriorityUtil;
import org.junit.Test;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThat;

public class PrioritySelectionTest {

  @Test
  public void checkFrequency() {
    List<Pair<PrioritySelection.Priority, PrioritySelection.Priority>> data =
        EnumSet.allOf(PrioritySelection.Priority.class).stream()
            .map(e -> new Pair<>(e, e))
            .collect(Collectors.toList());

    Selection<PrioritySelection.Priority> selection = new PrioritySelection<>(data);
    double numIterations = 1000;

    // when
    Map<PrioritySelection.Priority, Integer> nums = new HashMap<>();
    for (int i = 0; i < numIterations; i++) {
      PrioritySelection.Priority priority = selection.next();
      int num = nums.getOrDefault(priority, 0);
      nums.put(priority, num + 1);
    }

    // then
    for (PrioritySelection.Priority priority : PrioritySelection.Priority.values()) {
      double num = nums.getOrDefault(priority, 0);
      assertRange(priority, (int) (num / numIterations * 100d));
    }
  }

  private void assertRange(PrioritySelection.Priority priority, int result) {
    int chance =
        PriorityUtil.getChance(priority, EnumSet.allOf(PrioritySelection.Priority.class));
    assertThat(priority.name(), result, new RangeMatcher(chance - 5, chance + 5));
  }
}
