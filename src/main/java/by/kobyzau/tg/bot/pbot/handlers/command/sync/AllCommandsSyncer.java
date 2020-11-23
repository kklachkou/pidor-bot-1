package by.kobyzau.tg.bot.pbot.handlers.command.sync;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.NotBlankText;
import by.kobyzau.tg.bot.pbot.tg.action.SetMyCommandBotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component("AllCommandsSyncer")
public class AllCommandsSyncer implements CommandSyncer {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private Logger logger;

  @Override
  public void sync() {
    SetMyCommands setMyCommands = new SetMyCommands();
    List<BotCommand> botCommands =
        Arrays.stream(Command.values())
            .filter(c -> !c.getName().isEmpty())
            .sorted(Comparator.comparing(Command::getOrder))
            .map(this::map)
            .collect(Collectors.toList());
    setMyCommands.setCommands(botCommands);
    botActionCollector.add(new SetMyCommandBotAction(setMyCommands));
    logger.info("All commands are synced");
  }

  private BotCommand map(Command command) {
    BotCommand botCommand = new BotCommand();
    botCommand.setCommand(command.getName());
    botCommand.setDescription(new NotBlankText(command.getShortDesc(), "тест").text());
    return botCommand;
  }
}
