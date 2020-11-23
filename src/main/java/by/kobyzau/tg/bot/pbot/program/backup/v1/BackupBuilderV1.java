package by.kobyzau.tg.bot.pbot.program.backup.v1;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import by.kobyzau.tg.bot.pbot.program.backup.BackupBuilder;
import by.kobyzau.tg.bot.pbot.program.backup.DailyPidorBackupProcessor;
import by.kobyzau.tg.bot.pbot.program.backup.PidorBackupProcessor;

@Component
public class BackupBuilderV1 implements BackupBuilder {

  @Autowired
  private PidorBackupProcessor pidorBackupProcessor;

  @Autowired
  private DailyPidorBackupProcessor dailyPidorBackupProcessor;

  @Override
  public JSONObject buildBackup() {
    JSONObject backup = new JSONObject();
    backup.put("VERSION", getVersion());
    backup.put("PIDOR", pidorBackupProcessor.getData());
    backup.put("DAILY_PIDOR", dailyPidorBackupProcessor.getData());
    return backup;
  }

  @Override
  public int getVersion() {
    return 1;
  }
}
