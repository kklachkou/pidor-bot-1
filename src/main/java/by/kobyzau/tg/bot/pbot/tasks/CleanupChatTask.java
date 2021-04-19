package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.program.cleanup.CleanupHandler;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

@Component("cleanupChatTask")
public class CleanupChatTask implements Task {

  @Autowired private TelegramService telegramService;
  @Autowired private List<CleanupHandler> cleanupHandlers;
  @Autowired private Logger logger;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    cleanupHandlers.forEach(c -> executor.execute(c::cleanup));
  }
}
