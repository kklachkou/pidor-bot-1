package by.kobyzau.tg.bot.pbot.program.backup.v2;

import org.json.JSONArray;

public interface BackupProcessorV2 {

  JSONArray getData();

  void restoreFromBackup(JSONArray jsonArray);

  String getType();
}
