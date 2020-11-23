package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.program.logger.LoggerLevel;
import by.kobyzau.tg.bot.pbot.program.logger.LoggerLevelHolder;

@Component
public class LoggerLevelCommandHandler implements CommandHandler {

  @Autowired private LoggerLevelHolder loggerLevel;

  @Autowired
  private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    loggerLevel.setLevel(LoggerLevel.parseLevel(text).orElse(LoggerLevel.INFO));
    botActionCollector.text(message.getChatId(), new SimpleText("Logger: " + loggerLevel.getLevel()));
  }

  @Override
  public Command getCommand() {
    return Command.LOGGER;
  }
}
