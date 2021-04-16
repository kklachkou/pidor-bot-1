package by.kobyzau.tg.bot.pbot.program.backup.v1;

import org.json.JSONArray;

public interface BackupProcessorV1 {

  JSONArray getData();

  void restoreFromBackup(JSONArray jsonArray);

  String getType();
}
