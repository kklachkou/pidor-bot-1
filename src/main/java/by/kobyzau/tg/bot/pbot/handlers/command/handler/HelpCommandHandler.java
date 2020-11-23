package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.Comparator;

@Component
@Profile("prod")
public class HelpCommandHandler implements CommandHandler {
  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    botActionCollector.text(message.getChatId(), buildHelpMessage());
  }

  private Text buildHelpMessage() {
    return new TextBuilder(new SimpleText("Что умеет данный бот:"))
        .append(new NewLineText())
        .append(new NewLineText())
        .append(buildCommandsMessage(Command.Category.ACTION))
        .append(new NewLineText())
        .append(buildCommandsMessage(Command.Category.INFO))
        .append(new NewLineText())
        .append(new NewLineText())
        .append(new SimpleText("Автор - @NKRB2020"));
  }

  private Text buildCommandsMessage(Command.Category category) {
    TextBuilder tb = new TextBuilder();
    tb.append(new SimpleText("\t"))
        .append(new BoldText(category.getName()))
        .append(new NewLineText());
    Arrays.stream(Command.values())
        .filter(c -> c.getCategory() == category)
        .filter(this::filterCommand)
        .sorted(Comparator.comparing(Command::getOrder))
        .map(this::buildCommandMessage)
        .forEach(tb::append);
    return tb;
  }

  private Text buildCommandMessage(Command command) {
    return new TextBuilder(new SimpleText("\t\t▫️ /"))
        .append(new SimpleText(command.getName()))
        .append(new SpaceText())
        .append(new NotBlankText(command.getDesc()))
        .append(new NewLineText());
  }

  protected boolean filterCommand(Command command) {
    if (Command.NONE == command) {
      return false;
    }
    return StringUtil.isNotBlank(command.getDesc());
  }

  @Override
  public Command getCommand() {
    return Command.HELP;
  }
}
