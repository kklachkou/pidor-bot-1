package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.model.PidorOfYear;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.repository.pidorofyear.PidorOfYearRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class TestCommandHandler implements CommandHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private TelegramService telegramService;

  @Autowired private PidorService pidorService;
  @Autowired private PidorOfYearRepository repository;

  @Override
  public void processCommand(Message message, String text) {
    botActionCollector.text(message.getChatId(), new SimpleText("Test"));
    record2019();
    record2018();
    botActionCollector.text(message.getChatId(), new SimpleText("End"));
  }

  private void record2019() {
    PidorOfYear pidorOfYear = new PidorOfYear();
    pidorOfYear.setPlayerTgId(451337639);
    pidorOfYear.setChatId(-1001430668164L);
    pidorOfYear.setYear(2019);
    repository.create(pidorOfYear);
  }


  private void record2018() {
    PidorOfYear pidorOfYear = new PidorOfYear();
    pidorOfYear.setPlayerTgId(279424529);
    pidorOfYear.setChatId(-1001430668164L);
    pidorOfYear.setYear(2018);
    repository.create(pidorOfYear);
  }

  @Override
  public Command getCommand() {
    return Command.TEST;
  }
}
