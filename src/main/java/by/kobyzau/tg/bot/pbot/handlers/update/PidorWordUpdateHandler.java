package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.ParsedCommand;
import by.kobyzau.tg.bot.pbot.handlers.command.parser.CommandParser;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PidorWordUpdateHandler implements UpdateHandler {

  @Autowired private Logger logger;

  @Value("${bot.pidor.username}")
  private String botUserName;

  @Autowired private CommandParser commandParser;

  @Autowired private BotActionCollector botActionCollector;

  private static final List<String> KEYWORDS =
      Arrays.asList(
          "бот-пидор",
          "бот пидор",
          "пидор бот",
          "пидорора бот",
          "пидору бот",
          "пидоры бот",
          "пидор-бот",
          "бот-пидар",
          "бот пидар",
          "пидар бот",
          "пидар-бот");

  @Override
  public boolean handleUpdate(Update update) {
    if (!validateMessage(update)) {
      return false;
    }
    Message message = update.getMessage();
    String text = message.getText().trim().toLowerCase();

    if (KEYWORDS.stream().anyMatch(text::contains) || isUserNameUsed(update)) {
      botActionCollector.text(
          message.getChatId(),
          new RandomText(
              "Не поминай имени Пидор Бота твоего всуе",
              "Как ты смеешь произносить моё имя?",
              "Не смей произносить моё имя",
              "Не произноси имени Бота, Пидора твоего, напрасно, ибо Бот не оставит без наказания того, кто произносит имя Его напрасно"),
          message.getMessageId());
    }
    return false;
  }

  private boolean isUserNameUsed(Update update) {
    return !getCommand(update).isPresent()
        && update.getMessage().getText().toLowerCase().contains(botUserName.toLowerCase());
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
