package by.kobyzau.tg.bot.pbot.program.backup;

import org.json.JSONArray;

public interface BackupProcessor {

  JSONArray getData();

  void parseData(JSONArray jsonArray);
}
