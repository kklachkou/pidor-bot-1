package by.kobyzau.tg.bot.pbot.program.backup;

import org.json.JSONObject;

public interface BackupBuilder {

  JSONObject buildBackup();

  int getVersion();
}
