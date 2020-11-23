package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.HelpCommandHandler;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevHelpCommandHandler extends HelpCommandHandler {
  @Override
  protected boolean filterCommand(Command command) {
    return command != Command.NONE;
  }
}
