package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class TestCommandHandler implements CommandHandler {

  @Autowired private PidorRepository pidorRepository;

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    List<Pidor> pidors =
        pidorRepository.getAll().stream()
            .filter(p -> p.getTgId() == 419320050)
            .collect(Collectors.toList());
    for (Pidor pidor : pidors) {
      pidor.setFullName(pidor.getFullName().replaceAll("\uD83C\uDDE7\uD83C\uDDFE", "\uD83D\uDCA9"));
      pidor.setUsername(pidor.getUsername().replaceAll("\uD83C\uDDE7\uD83C\uDDFE", "\uD83D\uDCA9"));
      pidorRepository.update(pidor);
    }
    botActionCollector.text(message.getChatId(), new SimpleText("Done!"));
  }

  @Override
  public Command getCommand() {
    return Command.TEST;
  }
}
