package by.kobyzau.tg.bot.pbot.handlers.command.handler;


import org.telegram.telegrambots.meta.api.objects.Message;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;

public interface CommandHandler {

  void processCommand(Message message, String text);

  Command getCommand();
}
