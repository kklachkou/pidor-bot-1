package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.ParsedCommand;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandlerFactory;
import by.kobyzau.tg.bot.pbot.handlers.command.parser.CommandParser;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;

@Component
@Order(UpdateHandler.COMMAND_ORDER)
public class CommandUpdateHandler implements UpdateHandler {

  @Autowired private Logger logger;

  @Autowired private CommandHandlerFactory commandHandlerFactory;

  @Autowired private CommandParser commandParser;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private BotService botService;

  @Value("${app.admin.userId}")
  private int adminUserId;

  @Override
  public boolean handleUpdate(Update update) {
    if (!validateMessage(update)) {
      return false;
    }
    try {
      return processCommand(update) != Command.NONE;
    } catch (Exception e) {
      logger.error("Error in analazing", e);
    }
    return false;
  }

  private boolean validateMessage(Update update) {
    if (!update.hasMessage()) {
      return false;
    }
    Message message = update.getMessage();

    if (message.getChatId() == null) {
      return false;
    }
    if (!message.hasText()) {
      return false;
    }
    User from = message.getFrom();
    if (from == null) {
      return false;
    }
    return from.getId() != null;
  }

  private Command processCommand(Update update) {
    Message message = update.getMessage();

    ParsedCommand parsedCommand = commandParser.parseCommand(message.getText());
    if (isHiddenCommand(parsedCommand.getCommand())
        && !Objects.equals(message.getFrom().getId(), adminUserId)) {
      logger.warn(
          new ParametizedText(
                  "User {0} calls hidden command {1}",
                  new IntText(message.getFrom().getId()),
                  new SimpleText(parsedCommand.getCommand().name()))
              .text());
      return Command.NONE;
    }
    if (parsedCommand.getCommand() != Command.NONE) {
      if (!botService.isChatValid(message.getChatId())) {
        return Command.NONE;
      }
      botActionCollector.typing(message.getChatId());
      logger.debug(
              "\uD83D\uDECE New Command: " + parsedCommand + "\nFrom Update: " + update.getUpdateId());
    }
    CommandHandler commandHandler = commandHandlerFactory.getHandler(parsedCommand.getCommand());
    commandHandler.processCommand(message, parsedCommand.getText());
    return parsedCommand.getCommand();
  }

  private boolean isHiddenCommand(Command command) {
    return command != Command.NONE && StringUtil.isBlank(command.getDesc());
  }
}
