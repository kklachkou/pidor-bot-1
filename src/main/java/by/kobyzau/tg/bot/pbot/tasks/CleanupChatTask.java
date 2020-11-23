package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.model.ExcludeGameUserValue;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorDice;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.dice.DiceRepository;
import by.kobyzau.tg.bot.pbot.repository.exclude.ExcludeGameRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executor;

@Component("cleanupChatTask")
public class CleanupChatTask implements Task {

  @Autowired private TelegramService telegramService;
  @Autowired private PidorRepository pidorRepository;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private DiceRepository diceRepository;
  @Autowired private ExcludeGameRepository excludeGameRepository;
  @Autowired private Logger logger;

  @Autowired private BotService botService;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    if (DateUtil.now().getDayOfWeek() == DayOfWeek.WEDNESDAY) {
      telegramService.getChatIds().forEach(chatId -> executor.execute(() -> cleanup(chatId)));
    }
    executor.execute(this::cleanupDice);
  }

  private void cleanup(long chatId) {
    cleanupPidors(chatId);
    cleanupChats(chatId);
  }

  private void cleanupDice() {
    LocalDate startDate = DateUtil.now().minusWeeks(1);
    logger.debug("Cleaning old dices");
    diceRepository.getAll().stream()
        .filter(d -> d.getLocalDate().isBefore(startDate))
        .map(PidorDice::getId)
        .forEach(diceRepository::delete);
    logger.debug("Cleaning old exclude games");
    excludeGameRepository.getAll().stream()
        .filter(d -> d.getLocalDate().isBefore(startDate))
        .map(ExcludeGameUserValue::getId)
        .forEach(excludeGameRepository::delete);
    logger.debug("Cleaning games is ended");
  }

  private void cleanupChats(long chatId) {
    if (hasPidorLastFewDays(chatId)) {
      return;
    }
    if (!botService.isChatValid(chatId)) {
      logger.warn("Deleting inactive chat " + chatId);
      pidorRepository.getByChat(chatId).stream().map(Pidor::getId).forEach(pidorRepository::delete);
    }
  }

  private void cleanupPidors(long chatId) {
    List<Pidor> pidors = pidorRepository.getByChat(chatId);
    for (Pidor pidor : pidors) {
      if (!TGUtil.isChatMember(telegramService.getChatMember(chatId, pidor.getTgId()))) {
        logger.warn(
            new ParametizedText(
                    "Deleting inactive pidor {0} from chat {1}",
                    new FullNamePidorText(pidor), new SimpleText(String.valueOf(chatId)))
                .text());
        pidorRepository.delete(pidor.getId());
      }
    }
  }

  private boolean hasPidorLastFewDays(long chatId) {
    LocalDate today = DateUtil.now();
    LocalDate yesterday = today.minusDays(1);
    LocalDate beforeYesterday = yesterday.minusDays(1);
    return dailyPidorRepository.getByChatAndDate(chatId, today).isPresent()
        || dailyPidorRepository.getByChatAndDate(chatId, yesterday).isPresent()
        || dailyPidorRepository.getByChatAndDate(chatId, beforeYesterday).isPresent();
  }
}
