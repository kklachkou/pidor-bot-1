package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.bots.game.election.ElectionFinalizer;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component("electionEndTask")
public class ElectionEndTask implements Task {

  @Autowired private ElectionService electionService;
  @Autowired private PidorService pidorService;

  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private Logger logger;

  @Autowired private BotService botService;

  @Autowired private DailyPidorRepository dailyPidorRepository;

  @Autowired private ElectionFinalizer electionFinalizer;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    if (electionService.isElectionDay(DateUtil.now())) {
      logger.info("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
      telegramService.getChatIds().stream()
          .filter(botService::isChatValid)
          .forEach(chatId -> executor.execute(() -> electionFinalizer.finalize(chatId)));
    }
  }
}
