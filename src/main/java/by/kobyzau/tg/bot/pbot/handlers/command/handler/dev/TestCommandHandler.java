package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.concurrent.Executor;

@Component
public class TestCommandHandler implements CommandHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @Override
  public void processCommand(Message message, String text) {
    botActionCollector.text(message.getChatId(), new SimpleText("Out"));
    executor.execute(
        () -> {
          botActionCollector.text(message.getChatId(), new SimpleText("In"));
          throw new RuntimeException("Some error");
        });
  }

  @Override
  public Command getCommand() {
    return Command.TEST;
  }
}
