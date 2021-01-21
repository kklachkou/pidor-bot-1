package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.bots.game.exclude.ExcludeFinalizer;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.ExcludeGameService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.Executor;

@Component("excludeUserGameEndTask")
public class ExcludeUserGameEndTask implements Task {

  @Autowired private TelegramService telegramService;

  @Autowired private ExcludeFinalizer excludeFinalizer;
  @Autowired private ExcludeGameService gameService;
  @Autowired private Logger logger;

  @Autowired private BotService botService;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    logger.info("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    LocalDate now = DateUtil.now();
    telegramService.getChatIds().stream()
        .filter(botService::isChatValid)
        .filter(chatId -> gameService.isExcludeGameDay(chatId, now))
        .forEach(chatId -> executor.execute(() -> excludeFinalizer.finalize(chatId)));
  }
}
