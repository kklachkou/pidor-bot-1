package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.bots.LoggerBot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.program.backup.BackupService;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.BackupUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import java.io.ByteArrayInputStream;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class BackupCommandHandler implements CommandHandler {

  @Value("${logger.tg.chat}")
  private String backupChat;

  @Autowired private BackupService backupService;
  @Autowired private LoggerBot loggerBot;
  @Autowired private Logger logger;

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {

    if (message != null) {
      botActionCollector.chatAction(message.getChatId(), ChatAction.UPLOAD_DOC);
    }
    JSONObject json = backupService.buildBackup();
    try {
      loggerBot.execute(
          SendMessage.builder()
              .chatId(backupChat)
              .disableNotification(true)
              .text("#backup " + DateUtil.now() + "\nVersion: " + backupService.getVersion())
              .build());
      InputFile inputFile =
          new InputFile(
              new ByteArrayInputStream(json.toString().getBytes()),
              BackupUtil.getBackupFileName(backupService.getVersion()));
      loggerBot.execute(SendDocument.builder().chatId(backupChat).document(inputFile).build());
    } catch (TelegramApiException e) {
      logger.error("Cannot send Backup message", e);
    }
    if (message != null) {
      botActionCollector.text(message.getChatId(), new SimpleText("Backup created"));
    }
  }

  @Override
  public Command getCommand() {
    return Command.BACKUP;
  }
}
