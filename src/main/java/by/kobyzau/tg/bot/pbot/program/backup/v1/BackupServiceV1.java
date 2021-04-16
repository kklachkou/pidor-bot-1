package by.kobyzau.tg.bot.pbot.program.backup.v1;

import by.kobyzau.tg.bot.pbot.program.backup.BackupRestoreProgressListener;
import by.kobyzau.tg.bot.pbot.program.backup.BackupService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BackupServiceV1 implements BackupService {

  @Autowired
  private List<BackupProcessorV1> backupProcessorV1s;


  @Override
  public JSONObject buildBackup() {
    JSONObject backup = new JSONObject();
    backup.put("VERSION", getVersion());
    for (BackupProcessorV1 backupProcessorV1 : backupProcessorV1s) {
      backup.put(backupProcessorV1.getType(), backupProcessorV1.getData());
    }
    return backup;
  }

  @Override
  public void restoreFromBackup(JSONObject jsonObject, BackupRestoreProgressListener progressListener) {
    throw new UnsupportedOperationException("V1 restore is not supported");
  }

  @Override
  public int getVersion() {
    return 1;
  }
}
