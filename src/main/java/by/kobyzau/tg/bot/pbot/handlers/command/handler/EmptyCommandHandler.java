package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;

@Component
public class EmptyCommandHandler implements CommandHandler {

  @Override
  public void processCommand(Message message, String text) {
  }

  @Override
  public Command getCommand() {
    return Command.NONE;
  }
}
