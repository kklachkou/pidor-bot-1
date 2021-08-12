package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.handlers.future.FutureActionHandler;
import by.kobyzau.tg.bot.pbot.handlers.future.FutureActionHandlerFactory;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.service.FutureActionService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("futureActionTask")
public class FutureActionTask implements Task {

  @Autowired private Logger logger;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Autowired private FutureActionService futureActionService;

  @Autowired private FutureActionHandlerFactory futureActionHandlerFactory;

  @Override
  public void processTask() {
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    for (FutureActionService.FutureActionType type :
        FutureActionService.FutureActionType.values()) {
      executor.execute(() -> processFutureAction(type));
    }
  }

  private void processFutureAction(FutureActionService.FutureActionType type) {
    LocalDate now = DateUtil.now();
    Optional<FutureActionHandler> handler = futureActionHandlerFactory.getHandler(type);
    handler.ifPresent(
        h -> {
          List<String> futureActionData = futureActionService.getFutureActionData(type, now);
          futureActionData.forEach(h::processAction);
          futureActionService.removeFutureData(type, now);
        });
  }
}
