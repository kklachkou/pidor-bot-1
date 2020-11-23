package by.kobyzau.tg.bot.pbot.handlers.command.parser;

import by.kobyzau.tg.bot.pbot.handlers.command.ParsedCommand;

public interface CommandParser {

  ParsedCommand parseCommand(String message);
}
