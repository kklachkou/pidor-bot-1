package by.kobyzau.tg.bot.pbot.collectors;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ConcurrentReceiveUpdateCollector implements ReceiveUpdateCollector {

  private final Queue<Update> queue = new ConcurrentLinkedQueue<>();

  @Override
  public void collectUpdate(Update update) {
    queue.add(update);
  }

  @Override
  public Update poll() {
    return queue.poll();
  }
}
