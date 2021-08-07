package by.kobyzau.tg.bot.pbot.handlers.command.sync;

import by.kobyzau.tg.bot.pbot.bots.PidorBot;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Primary
public class CommandSyncerImpl implements CommandSyncer {

  @Value("${app.admin.userId}")
  private long adminUserId;

  @Autowired private PidorBot pidorBot;
  @Autowired private Logger logger;

  @Override
  public void sync() {
    List<BotCommand> defaultCommands =
        Arrays.stream(Command.values())
            .filter(c -> c.getShortDesc() != null)
            .sorted(Comparator.comparing(Command::getOrder))
            .map(this::map)
            .collect(Collectors.toList());

    List<BotCommand> allCommands =
        Arrays.stream(Command.values())
            .filter(c -> !c.getName().isEmpty())
            .sorted(Comparator.comparing(Command::getOrder))
            .map(this::map)
            .collect(Collectors.toList());
    try {
      pidorBot.execute(SetMyCommands.builder().commands(defaultCommands).build());
    } catch (TelegramApiException e) {
      logger.error("Cannot setup commands", e);
    }

    try {
      pidorBot.execute(
          SetMyCommands.builder()
              .commands(allCommands)
              .scope(BotCommandScopeChat.builder().chatId(String.valueOf(adminUserId)).build())
              .build());
    } catch (TelegramApiException e) {
      logger.error("Cannot setup commands", e);
    }
  }

  private BotCommand map(Command command) {
    BotCommand botCommand = new BotCommand();
    botCommand.setCommand(command.getName());
    botCommand.setDescription(command.getShortDesc());
    return botCommand;
  }
}
