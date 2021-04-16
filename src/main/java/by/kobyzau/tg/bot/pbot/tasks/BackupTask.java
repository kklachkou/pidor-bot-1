package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandlerFactory;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("backupTask")
public class BackupTask implements Task {

  @Autowired private Logger logger;

  @Autowired private CommandHandlerFactory commandHandlerFactory;

  @Override
  public void processTask() {
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    createBackup();
  }

  private void createBackup() {
    commandHandlerFactory.getHandler(Command.BACKUP).processCommand(null, null);
  }
}
