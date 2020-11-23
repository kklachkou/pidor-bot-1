package by.kobyzau.tg.bot.pbot.program.selection;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleSelection<T> implements Selection<T> {

  private final Queue<T> queue;

  public SimpleSelection(T... originalList) {
    this(Arrays.asList(originalList));
  }

  public SimpleSelection(List<T> originalList) {
    this.queue = new ConcurrentLinkedQueue<>();
    this.queue.addAll(originalList);
  }

  @Override
  public T next() {
    if (queue.isEmpty()) {
      return null;
    }
    return queue.poll();
  }
}
