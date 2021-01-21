package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.tasks.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SystemCheckCommandHandler implements CommandHandler {

  @Autowired private Task systemCheckTask;

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    systemCheckTask.processTask();
    botActionCollector.text(message.getChatId(), new SimpleText("System check is started"));
  }

  @Override
  public Command getCommand() {
    return Command.SYSTEM_CHECK;
  }
}
