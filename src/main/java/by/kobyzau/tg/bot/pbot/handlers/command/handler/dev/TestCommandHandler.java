package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class TestCommandHandler implements CommandHandler {

  @Autowired private PidorRepository pidorRepository;

  @Override
  public void processCommand(Message message, String text) {
    for (Pidor pidor : pidorRepository.getAll()) {
      pidor.setUsernameLastUpdated(DateUtil.now());
      pidorRepository.update(pidor);
    }
  }

  @Override
  public Command getCommand() {
    return Command.TEST;
  }
}
