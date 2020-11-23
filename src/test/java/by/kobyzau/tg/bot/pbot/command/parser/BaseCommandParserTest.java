package by.kobyzau.tg.bot.pbot.command.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import by.kobyzau.tg.bot.pbot.handlers.command.parser.BaseCommandParser;
import by.kobyzau.tg.bot.pbot.handlers.command.parser.CommandParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.ParsedCommand;

@RunWith(Parameterized.class)
public class BaseCommandParserTest {

  private static final String BOT_NAME = "myBot";

  @Parameterized.Parameter public String message;

  @Parameterized.Parameter(1)
  public ParsedCommand parsedCommand;

  private CommandParser parser;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    List<Object[]> params = new ArrayList<>();
    params.add(new Object[] {"", ParsedCommand.getNone()});
    params.add(new Object[] {null, ParsedCommand.getNone()});
    params.add(new Object[] {"some text, not command", ParsedCommand.getNone()});
    params.add(new Object[] {"/pidor@notmybotname", ParsedCommand.getNone()});
    params.add(new Object[] {"/pidor", new ParsedCommand(Command.PIDOR, "")});
    params.add(new Object[] {"/pidor some text", new ParsedCommand(Command.PIDOR, "some text")});
    params.add(
        new Object[] {
          "/pidor@" + BOT_NAME + " some text", new ParsedCommand(Command.PIDOR, "some text")
        });
    params.add(new Object[] {"/pidor@" + BOT_NAME, new ParsedCommand(Command.PIDOR, "")});
    return params;
  }

  @Before
  public void init() {
    parser = new BaseCommandParser(BOT_NAME);
  }

  @Test
  public void parseCommand() {

    ParsedCommand parsedCommand = parser.parseCommand(message);

    Assert.assertEquals(this.parsedCommand, parsedCommand);
  }
}
