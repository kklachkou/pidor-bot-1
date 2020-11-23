package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;

@Component
public class CommandHandlerFactoryImpl implements CommandHandlerFactory {

  @Autowired private List<CommandHandler> handlers;
  private Map<Command, CommandHandler> map;

  @PostConstruct
  private void init() {
    map = new HashMap<>();
    handlers.forEach(h -> map.put(h.getCommand(), h));
  }

  @Override
  public CommandHandler getHandler(Command command) {
    return Optional.ofNullable(map.get(command)).orElseGet(() -> map.get(Command.NONE));
  }
}
