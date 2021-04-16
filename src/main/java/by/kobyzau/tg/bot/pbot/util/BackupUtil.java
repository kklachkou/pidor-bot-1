package by.kobyzau.tg.bot.pbot.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackupUtil {

  private static final Pattern BACKUP_FILE_PATTERN =
      Pattern.compile("pidor-bot-backup-V(\\d)-\\d{4}-\\d{2}-\\d{2}.json");

  public static String getBackupFileName(int version) {
    return "pidor-bot-backup-V" + version + "-" + DateUtil.now() + ".json";
  }

  public static int getBackupVersionFromFileName(String fileName) {
    Matcher matcher = BACKUP_FILE_PATTERN.matcher(fileName);
    if (matcher.matches()) {
      return StringUtil.parseInt(matcher.group(1), 0);
    }
    return 0;
  }
}
