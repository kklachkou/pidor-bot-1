package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.program.checker.SystemChecker;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

@Component("systemCheckTask")
public class SystemCheckTask implements Task {

  @Autowired private List<SystemChecker> systemCheckers;
  @Autowired private Logger logger;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    executor.execute(this::checkSystem);
  }

  private void checkSystem() {
    systemCheckers.forEach(SystemChecker::check);
  }
}
