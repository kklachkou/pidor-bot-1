package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.program.backup.BackupProgress;
import by.kobyzau.tg.bot.pbot.program.backup.BackupService;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.util.BackupUtil;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Order
@Component
public class BackupUpdateHandler implements UpdateHandler {

  @Autowired private BackupService backupService;
  @Autowired private Bot bot;
  @Autowired private Logger logger;

  @Value("${app.admin.userId}")
  private long adminUserId;

  @Override
  public boolean handleUpdate(Update update) {
    if (!update.hasMessage()) {
      return false;
    }
    Message message = update.getMessage();
    if (message.getFrom() == null || message.getFrom().getId() != adminUserId) {
      return false;
    }

    if (!message.hasDocument()) {
      return false;
    }
    Document document = message.getDocument();
    String fileName = document.getFileName();
    int version = BackupUtil.getBackupVersionFromFileName(fileName);
    if (backupService.getVersion() != version) {
      return false;
    }
    Optional<File> file = bot.getFile(document.getFileId());
    if (!file.isPresent()) {
      return false;
    }
    JSONObject json = getJson(file.get());
    try {
      Message backupMessage =
          bot.execute(
              SendMessage.builder()
                  .chatId(String.valueOf(message.getChatId()))
                  .text("Start backup process")
                  .build());
      backupService.restoreFromBackup(
          json, backupProgress -> printProgress(backupProgress, backupMessage));

    } catch (Exception e) {
      logger.error("Cannot restore backup", e);
    }

    return true;
  }

  private void printProgress(BackupProgress progress, Message backupMessage) {
    Map<String, String> statePerType = progress.getStatePerType();
    StringBuilder sb = new StringBuilder();
    sb.append("Backup progress\n");
    for (Map.Entry<String, String> entry :
        statePerType.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toList())) {
      sb.append("\n").append(entry.getKey()).append(": ").append(entry.getValue());
    }
    try {
      bot.execute(
          EditMessageText.builder()
              .chatId(String.valueOf(backupMessage.getChatId()))
              .messageId(backupMessage.getMessageId())
              .text(sb.toString())
              .build());
    } catch (Exception innerExc) {
      logger.error("Cannot update message", innerExc);
    }
  }

  private JSONObject getJson(File file) {
    String fileUrl = file.getFileUrl(bot.getBotToken());

    try {
      java.io.File tempFile = createTempFile("p-bot-" + file.getFileId());
      FileUtils.copyURLToFile(new URL(fileUrl), tempFile);
      return new JSONObject(FileUtils.readFileToString(tempFile, Charset.defaultCharset()));
    } catch (Exception e) {
      logger.error("Cannot get json from backup", e);
      return new JSONObject();
    }
  }

  private java.io.File createTempFile(String fileName) {
    try {
      Path tempDirectory = Files.createTempDirectory("pbot-backup");
      return new java.io.File(tempDirectory.toFile(), fileName);
    } catch (Exception e) {
      throw new RuntimeException("Cannot create temp file", e);
    }
  }
}
