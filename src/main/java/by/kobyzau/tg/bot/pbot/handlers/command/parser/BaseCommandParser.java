package by.kobyzau.tg.bot.pbot.handlers.command.parser;

import by.kobyzau.tg.bot.pbot.handlers.command.ParsedCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.util.StringUtil;

@Component
public class BaseCommandParser implements CommandParser {

  private final String PREFIX_FOR_COMMAND = "/";
  private final String DELIMITER_COMMAND_BOTNAME = "@";

  @Value("${bot.username}")
  private String botName;

  public BaseCommandParser() {}

  public BaseCommandParser(String botName) {
    this.botName = botName;
  }

  @Override
  public ParsedCommand parseCommand(String message) {
    if (StringUtil.isBlank(message)) {
      return ParsedCommand.getNone();
    }
    String trimText = StringUtil.trim(message);

    Pair<String, String> commandAndText = getDelimitedCommandFromText(trimText);
    if (isCommand(commandAndText.getLeft())) {
      if (isCommandForMe(commandAndText.getLeft())) {
        String commandForParse = cutCommandFromFullText(commandAndText.getLeft());
        Command commandFromText = Command.parseCommand(commandForParse).orElse(Command.NONE);
        return new ParsedCommand(commandFromText, commandAndText.getRight());
      } else {
        return ParsedCommand.getNone();
      }
    }
    return ParsedCommand.getNone();
  }

  private String cutCommandFromFullText(String text) {
    return text.contains(DELIMITER_COMMAND_BOTNAME)
        ? text.substring(1, text.indexOf(DELIMITER_COMMAND_BOTNAME))
        : text.substring(1);
  }

  private Pair<String, String> getDelimitedCommandFromText(String trimText) {
    Pair<String, String> commandText;

    if (trimText.contains(" ")) {
      int indexOfSpace = trimText.indexOf(" ");
      commandText =
          new Pair<>(trimText.substring(0, indexOfSpace), trimText.substring(indexOfSpace + 1));
    } else commandText = new Pair<>(trimText, "");
    return commandText;
  }

  private boolean isCommandForMe(String command) {
    if (command.contains(DELIMITER_COMMAND_BOTNAME)) {
      String botNameForEqual = command.substring(command.indexOf(DELIMITER_COMMAND_BOTNAME) + 1);
      return botName.equals(botNameForEqual);
    }
    return true;
  }

  private boolean isCommand(String text) {
    return text.startsWith(PREFIX_FOR_COMMAND);
  }
}
