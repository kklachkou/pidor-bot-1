package by.kobyzau.tg.bot.pbot.program.selection;

import by.kobyzau.tg.bot.pbot.util.CollectionUtil;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HalfSelection<T> implements Selection<T> {

  private final int minQueueSize;
  private final List<T> originalList;
  private final Queue<T> queue;

  public HalfSelection(List<T> originalList) {
    this.originalList = Collections.unmodifiableList(originalList);
    this.minQueueSize = calcMinQueueSize(originalList);
    this.queue = new ConcurrentLinkedQueue<>();
    this.queue.addAll(CollectionUtil.getRandomList(this.originalList));
  }

  private int calcMinQueueSize(List<T> list) {
    int size = CollectionUtil.size(list);
    if (size < 5) {
      return size;
    }
    return size / 2;
  }

  @Override
  public T next() {
    if (queue.size() <= minQueueSize) {
      queue.clear();
      this.queue.addAll(CollectionUtil.getRandomList(originalList));
    }
    return queue.poll();
  }
}
