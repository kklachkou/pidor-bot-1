package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.PidorFunnyAction;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.HotPotatoesService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

@Component("hotPotatoCheckTask")
public class HotPotatoCheckTask implements Task {
  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private HotPotatoesService hotPotatoesService;

  @Autowired private BotService botService;

  @Autowired private Logger logger;
  @Autowired private PidorRepository pidorRepository;

  @Autowired private DailyPidorRepository dailyPidorRepository;

  @Autowired private List<PidorFunnyAction> pidorFunnyActions;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    LocalDate now = DateUtil.now();
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    pidorRepository.getChatIdsWithPidors().stream()
        .distinct()
        .filter(chatId -> hotPotatoesService.isHotPotatoesDay(chatId, now))
        .forEach(chatId -> executor.execute(() -> sendMessage(chatId)));
  }

  private void sendMessage(long chatId) {
    LocalDate now = DateUtil.now();
    if (dailyPidorRepository.getByChatAndDate(chatId, now).isPresent()) {
      return;
    }
    Optional<Pidor> lastTaker = hotPotatoesService.getLastTaker(now, chatId);
    Optional<LocalDateTime> lastTakerDeadline =
        hotPotatoesService.getLastTakerDeadline(now, chatId);
    if (!lastTaker.isPresent() || !lastTakerDeadline.isPresent()) {
      return;
    }
    LocalDateTime currentTime = DateUtil.currentTime();
    if (currentTime.isAfter(lastTakerDeadline.get())) {
      saveDailyPidor(lastTaker.get());
      botActionCollector.text(
          chatId, new SimpleText("Время вышло! Картошечка сгорела в чьих-то руках!"));
      botActionCollector.wait(chatId, 5, ChatAction.TYPING);
      botService.unpinLastBotMessage(chatId);
      CollectionUtil.getRandomValue(pidorFunnyActions).processFunnyAction(chatId, lastTaker.get());
    }
  }

  private void saveDailyPidor(Pidor pidor) {
    DailyPidor dailyPidor = new DailyPidor();
    dailyPidor.setChatId(pidor.getChatId());
    dailyPidor.setPlayerTgId(pidor.getTgId());
    dailyPidor.setLocalDate(DateUtil.now());
    dailyPidorRepository.create(dailyPidor);
  }
}
