package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.ParsedCommand;
import by.kobyzau.tg.bot.pbot.handlers.command.parser.CommandParser;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class InvalidChatUpdateHandler implements UpdateHandler {

  @Autowired private BotService botService;
  @Autowired private CommandParser commandParser;
  @Autowired private BotActionCollector botActionCollector;

  @Override
  public boolean test(LocalDate date) {
    return true;
  }

  @Override
  public boolean handleUpdate(Update update) {
    if (update.hasMessage()
        && !botService.isChatValid(update.getMessage().getChatId())
        && getCommand(update).isPresent()) {
      botActionCollector.text(
          update.getMessage().getChatId(), new SimpleText("Бот работает только в групповых чатах"));
    }

    return false;
  }

  private Optional<Command> getCommand(Update update) {
    if (!update.hasMessage()) {
      return Optional.empty();
    }
    Message message = update.getMessage();
    if (message == null) {
      return Optional.empty();
    }
    if (!message.hasText()) {
      return Optional.empty();
    }
    ParsedCommand parsedCommand = commandParser.parseCommand(message.getText());
    return Optional.ofNullable(parsedCommand)
        .map(ParsedCommand::getCommand)
        .filter(c -> c != Command.NONE);
  }
}
