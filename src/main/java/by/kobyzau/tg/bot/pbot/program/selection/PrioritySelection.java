package by.kobyzau.tg.bot.pbot.program.selection;

import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PrioritySelection<T> implements Selection<T> {

  private final List<Pair<T, Priority>> originalList;
  private final Queue<T> queue;

  public PrioritySelection(Pair<T, Priority>... originalList) {
    this(Arrays.asList(originalList));
  }

  public PrioritySelection(List<Pair<T, Priority>> originalList) {
    this.originalList = Collections.unmodifiableList(originalList);
    this.queue = new ConcurrentLinkedQueue<>();
    this.queue.addAll(getShuffledListAccordingPriority());
  }

  @Override
  public T next() {
    if (queue.isEmpty()) {
      this.queue.addAll(getShuffledListAccordingPriority());
    }
    return queue.poll();
  }

  private List<T> getShuffledListAccordingPriority() {
    List<T> result = new ArrayList<>();
    for (Pair<T, Priority> pair : originalList) {
      for (int i = 0; i < pair.getRight().copies; i++) {
        result.add(pair.getLeft());
      }
    }
    return CollectionUtil.getRandomList(result);
  }

  public enum Priority {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    HIGHEST(5);

    private final int copies;

    Priority(int copies) {
      this.copies = copies;
    }

    public int getCopies() {
      return copies;
    }
  }
}
