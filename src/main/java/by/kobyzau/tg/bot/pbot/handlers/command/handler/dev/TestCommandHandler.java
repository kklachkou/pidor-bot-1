package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.tasks.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class TestCommandHandler implements CommandHandler {

  @Autowired private Task updatePidorTask;

  @Override
  public void processCommand(Message message, String text) {
    updatePidorTask.processTask();
  }

  @Override
  public Command getCommand() {
    return Command.TEST;
  }
}
