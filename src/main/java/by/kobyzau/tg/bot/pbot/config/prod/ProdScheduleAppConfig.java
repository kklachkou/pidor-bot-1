package by.kobyzau.tg.bot.pbot.config.prod;

import by.kobyzau.tg.bot.pbot.tasks.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
@Profile("prod")
public class ProdScheduleAppConfig {

  @Autowired private Task pingHeroku;

  @Autowired private Task updateUsernameTask;
  @Autowired private Task backupTask;
  @Autowired private Task cleanupChatTask;
  @Autowired private Task pidorOfYearTask;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;


  @Scheduled(cron = "${task.pidorOfYear.cron}", zone = "GMT+3.00")
  public void pidorOfYearTask() {
    executor.execute(pidorOfYearTask::processTask);
  }

  @Scheduled(fixedRateString = "${task.pingHeroku.delay:60000}")
  public void processUpdates() {
    executor.execute(pingHeroku::processTask);
  }

  @Scheduled(cron = "${task.backup.cron}", zone = "GMT+3.00")
  public void backupTask() {
    executor.execute(backupTask::processTask);
    executor.execute(cleanupChatTask::processTask);
  }

  @Scheduled(cron = "${task.updateUsername.cron}", zone = "GMT+3.00")
  public void updateUsernameTask() {
    executor.execute(updateUsernameTask::processTask);
  }

}
