package by.kobyzau.tg.bot.pbot.program.backup.v2;

import by.kobyzau.tg.bot.pbot.program.backup.BackupProgress;
import by.kobyzau.tg.bot.pbot.program.backup.BackupRestoreProgressListener;
import by.kobyzau.tg.bot.pbot.program.backup.BackupService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class BackupServiceV2 implements BackupService {

  @Autowired private List<BackupProcessorV2> backupProcessors;

  @Override
  public JSONObject buildBackup() {
    JSONObject backup = new JSONObject();
    backup.put("VERSION", getVersion());
    for (BackupProcessorV2 backupProcessorV1 : backupProcessors) {
      backup.put(backupProcessorV1.getType(), backupProcessorV1.getData());
    }
    return backup;
  }

  @Override
  public void restoreFromBackup(
      JSONObject jsonObject, BackupRestoreProgressListener progressListener) {
    BackupProgress progress = new BackupProgress();
    for (BackupProcessorV2 backupProcessor : backupProcessors) {
      progress.addState(backupProcessor.getType(), "☑️");
    }
    for (BackupProcessorV2 backupProcessor : backupProcessors) {
      progress.addState(backupProcessor.getType(), "\uD83D\uDD54");
      progressListener.submitProgress(progress);
      if (jsonObject.has(backupProcessor.getType())) {
        backupProcessor.restoreFromBackup(jsonObject.getJSONArray(backupProcessor.getType()));
      }
      progress.addState(backupProcessor.getType(), "✅");
      progressListener.submitProgress(progress);
    }
  }

  @Override
  public int getVersion() {
    return 2;
  }
}
