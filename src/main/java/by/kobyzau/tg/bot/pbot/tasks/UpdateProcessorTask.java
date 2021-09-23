package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.ReceiveUpdateCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.printer.UpdatePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component("updateProcessorTask")
public class UpdateProcessorTask implements Task {

  @Autowired private ReceiveUpdateCollector updateCollector;

  @Autowired private List<UpdateHandler> updateHandlers;
  @Autowired private Logger logger;
  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @PostConstruct
  public void init() {
    updateHandlers =
        updateHandlers.stream()
            .sorted(Comparator.comparingInt(u -> u.getStage().getOrder()))
            .collect(Collectors.toList());
  }

  @Override
  public void processTask() {
    Update object = updateCollector.poll();
    while (object != null) {
      Update updateToHandle = object;
      executor.execute(() -> process(updateToHandle));
      object = updateCollector.poll();
    }
  }

  private void process(Update update) {
    for (UpdateHandler updateHandler : updateHandlers) {
      try {
        if (updateHandler.handleUpdate(update)) {
          return;
        }
      } catch (Exception e) {
        logger.error(
            "Error in UpdateProcessor for update:\n\n<pre>" + new UpdatePrinter(update) + "</pre>",
            e);
      }
    }
  }
}
