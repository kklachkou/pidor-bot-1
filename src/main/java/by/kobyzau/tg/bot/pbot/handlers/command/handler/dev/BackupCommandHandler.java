package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.program.backup.BackupService;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.TelegramSender;
import by.kobyzau.tg.bot.pbot.util.BackupUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class BackupCommandHandler implements CommandHandler {

  @Value("${logger.tg.token}")
  private String backupBotToken;

  @Value("${logger.tg.chat}")
  private String backupChat;

  @Autowired private BackupService backupService;

  @Autowired private TelegramSender telegramSender;

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    if (message != null) {
      botActionCollector.chatAction(message.getChatId(), ChatAction.UPLOAD_DOC);
    }

    JSONObject json = backupService.buildBackup();
    telegramSender.sendMessage(
        backupBotToken,
        backupChat,
        "#backup " + DateUtil.now() + "\nVersion: " + backupService.getVersion());
    telegramSender.sendStringAsFile(
        backupBotToken,
        backupChat,
        BackupUtil.getBackupFileName(backupService.getVersion()),
        json.toString());
    if (message != null) {
      botActionCollector.text(message.getChatId(), new SimpleText("Backup created"));
    }
  }

  @Override
  public Command getCommand() {
    return Command.BACKUP;
  }
}
