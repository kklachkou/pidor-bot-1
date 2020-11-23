package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;

public interface CommandHandlerFactory {

  CommandHandler getHandler(Command command);
}
