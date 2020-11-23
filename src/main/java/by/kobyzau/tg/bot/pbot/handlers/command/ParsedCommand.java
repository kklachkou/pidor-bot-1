package by.kobyzau.tg.bot.pbot.handlers.command;

import java.util.Objects;

public class ParsedCommand {
  private final Command command;
  private final String text;

  public ParsedCommand(Command command, String text) {
    this.command = command;
    this.text = text;
  }

  public static ParsedCommand getNone() {
    return new ParsedCommand(Command.NONE, "");
  }

  public Command getCommand() {
    return command;
  }

  public String getText() {
    return text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ParsedCommand that = (ParsedCommand) o;
    return command == that.command && Objects.equals(text, that.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(command, text);
  }

  @Override
  public String toString() {
    return "ParsedCommand[" + command + ", " + text +']';
  }
}
