package by.kobyzau.tg.bot.pbot.program.backup;

import org.json.JSONObject;

public interface BackupService {

  JSONObject buildBackup();

  void restoreFromBackup(JSONObject jsonObject, BackupRestoreProgressListener progressListener);


  int getVersion();
}
