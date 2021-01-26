package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.program.printer.SettingsCommandPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class ChatSettingsCommandHandler implements CommandHandler {

  @Autowired private SettingsCommandPrinter settingsCommandPrinter;

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    settingsCommandPrinter.printSettings(chatId);
  }
  @Override
  public Command getCommand() {
    return Command.SETTINGS;
  }
}
