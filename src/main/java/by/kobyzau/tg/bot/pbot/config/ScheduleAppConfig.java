package by.kobyzau.tg.bot.pbot.config;

import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.tasks.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
public class ScheduleAppConfig {

  @Autowired private Logger logger;
  @Autowired private Task updateProcessorTask;
  @Autowired private Task notifyNoPidors;
  @Autowired private Task notifyPidorOfTheMonthTask;
  @Autowired private Task diceStart;
  @Autowired private Task assassinTask;
  @Autowired private Task diceEnd;

  @Autowired private Task excludeUserGameStartTask;
  @Autowired private Task excludeUserGameEndTask;

  @Autowired private Task gameReminderTask;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @Scheduled(fixedDelayString = "${task.receiveUpdates.delay}")
  public void processUpdates() {
    try {
      updateProcessorTask.processTask();
    } catch (Exception e) {
      logger.error("processUpdates error", e);
    }
  }

  @Scheduled(cron = "${task.notifyNoPidors.cron}", zone = "GMT+3.00")
  public void notifyNoPidors() {
    executor.execute(notifyNoPidors::processTask);
  }

  @Scheduled(cron = "${task.notifyPidorOfTheMonthTask.cron}", zone = "GMT+3.00")
  public void notifyPidorOfTheMonthTask() {
    executor.execute(notifyPidorOfTheMonthTask::processTask);
  }

  @Scheduled(cron = "${task.gameReminderTask.cron}", zone = "GMT+3.00")
  public void gameReminderTask() {
    executor.execute(gameReminderTask::processTask);
  }

  @Scheduled(cron = "${task.gameStart.cron}", zone = "GMT+3.00")
  public void gameStart() {
    executor.execute(diceStart::processTask);
    executor.execute(excludeUserGameStartTask::processTask);
    executor.execute(assassinTask::processTask);
  }

  @Scheduled(cron = "${task.gameEnd.cron}", zone = "GMT+3.00")
  public void gameEnd() {
    executor.execute(diceEnd::processTask);
    executor.execute(excludeUserGameEndTask::processTask);
  }
}
