package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.program.backup.BackupBuilder;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.TelegramSender;
import by.kobyzau.tg.bot.pbot.util.DateUtil;

@Component
public class BackupCommandHandler implements CommandHandler {

  @Value("${logger.tg.bot.token}")
  private String backupBotToken;

  @Value("${logger.tg.chat}")
  private String backupChat;

  @Autowired private BackupBuilder backupBuilder;

  @Autowired private TelegramSender telegramSender;

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    if (message != null) {
      botActionCollector.chatAction(message.getChatId(), ChatAction.UPLOAD_DOC);
    }

    JSONObject json = backupBuilder.buildBackup();
    telegramSender.sendMessage(
        backupBotToken,
        backupChat,
        "#backup " + DateUtil.now() + "\nVersion: " + backupBuilder.getVersion());
    telegramSender.sendStringAsFile(
        backupBotToken,
        backupChat,
        "backup-V" + +backupBuilder.getVersion() + "-" + DateUtil.now() + ".json",
        json.toString());
    if (message != null) {
      botActionCollector.collectHTMLMessage(message.getChatId(), "Backup created");
    }
  }

  @Override
  public Command getCommand() {
    return Command.BACKUP;
  }
}
