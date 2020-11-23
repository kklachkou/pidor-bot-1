package by.kobyzau.tg.bot.pbot.program.selection;

import by.kobyzau.tg.bot.pbot.program.shuffler.Shuffler;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConsistentSelection<T> implements Selection<T> {

  private final List<T> originalList;
  private final Queue<T> queue;

  private final Shuffler<T> shuffler;

  public ConsistentSelection(T... originalList) {
    this(Arrays.asList(originalList), CollectionUtil::getRandomList);
  }
  public ConsistentSelection(List<T> originalList) {
    this(originalList, CollectionUtil::getRandomList);
  }

  public ConsistentSelection(Shuffler<T> shuffler, List<T> originalList) {
    this(originalList, shuffler);
  }

  public ConsistentSelection(List<T> originalList, Shuffler<T> shuffler) {
    this.originalList = Collections.unmodifiableList(originalList);
    this.queue = new ConcurrentLinkedQueue<>();
    this.queue.addAll(shuffler.apply(this.originalList));
    this.shuffler = shuffler;
  }

  @Override
  public T next() {
    if (queue.isEmpty()) {
      this.queue.addAll(shuffler.apply(originalList));
    }
    return queue.poll();
  }
}
