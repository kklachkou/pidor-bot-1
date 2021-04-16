package by.kobyzau.tg.bot.pbot.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BackupUtilTest {

  @Test
  public void getBackupFileName() {
    // given
    int version = 4;

    // when
    String backupFileName = BackupUtil.getBackupFileName(version);

    // then
    assertEquals("pidor-bot-backup-V" + version + "-" + DateUtil.now() + ".json", backupFileName);
  }

  @Test
  public void getBackupVersionFromFileName_anotherFormat() {
    // given
    String name = "anotherFormat";

    // when
    int version = BackupUtil.getBackupVersionFromFileName(name);

    // then
    assertEquals(0, version);
  }

  @Test
  public void getBackupVersionFromFileName_correctFormat() {
    // given
    String name = "pidor-bot-backup-V4-" + DateUtil.now() + ".json";

    // when
    int version = BackupUtil.getBackupVersionFromFileName(name);

    // then
    assertEquals(4, version);
  }

  @Test
  public void getBackupVersionFromFileName_fromCreated() {
    // given
    int version = 4;

    // when
    int parsedVersion =
        BackupUtil.getBackupVersionFromFileName(BackupUtil.getBackupFileName(version));

    // then
    assertEquals(version, parsedVersion);
  }
}
