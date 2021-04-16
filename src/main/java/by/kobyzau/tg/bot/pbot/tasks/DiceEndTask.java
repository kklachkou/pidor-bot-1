package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.bots.game.dice.DiceFinalizer;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.DiceService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.Executor;

@Component("diceEnd")
public class DiceEndTask implements Task {

  @Autowired private DiceFinalizer diceFinalizer;

  @Autowired private DailyPidorRepository dailyPidorRepository;

  @Autowired private DiceService diceService;

  @Autowired private TelegramService telegramService;

  @Autowired private Logger logger;

  @Autowired private BotService botService;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    LocalDate now = DateUtil.now();
    telegramService.getChatIds().stream()
            .filter(botService::isChatValid)
            .filter(chatId -> diceService.getGame(chatId, now).isPresent())
            .forEach(chatId -> executor.execute(() -> doDice(chatId)));
  }

  private void doDice(long chatId) {
    try {
      LocalDate now = DateUtil.now();
      if (dailyPidorRepository.getByChatAndDate(chatId, now).isPresent()) {
        return;
      }
      diceFinalizer.finalize(chatId);
    } catch (Exception e) {
      logger.error("Cannot end dice for chat " + chatId, e);
    }
  }
}
