package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.PidorFunnyAction;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNameLinkedPidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.HotPotatoesService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.HotPotatoUtil;
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

  @Autowired private HotPotatoUtil hotPotatoUtil;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private HotPotatoesService hotPotatoesService;

  @Autowired private BotService botService;

  @Autowired private Logger logger;
  @Autowired private PidorRepository pidorRepository;

  @Autowired private UserArtifactService userArtifactService;

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
    if (hotPotatoUtil.shouldRemind(lastTakerDeadline.get(), currentTime)) {
      botActionCollector.text(
          chatId,
          new ParametizedText(
              new RandomText(
                  "{0} - ещё чуть-чуть и картошечка сгорит у тебя",
                  "{0} - брось её!",
                  "{0} - сейчас картоха подпалит твой пукан",
                  "{0} - поторопись, картошечка почти догорела",
                  "{0} - ещё немного и картошечка сгорит у тебя",
                  "Напомните {0}, что у него догорает карточека",
                  "{0} в мгновении от пройгрыша..",
                  "Ух, сейчас у {0} догорит картошечка..."),
              new ShortNameLinkedPidorText(lastTaker.get())));
      botActionCollector.sticker(chatId, StickerType.ALMOST_DEAD.getRandom());
    }
    if (currentTime.isAfter(lastTakerDeadline.get())) {
      saveDailyPidor(lastTaker.get());
      userArtifactService.clearUserArtifacts(chatId, ArtifactType.PIDOR_MAGNET);
      userArtifactService.clearUserArtifacts(chatId, ArtifactType.HELL_FIRE);
      botActionCollector.text(
          chatId,
          new ParametizedText(
              "{0}! {1}",
              new RandomText("Время вышло", "Ну вот и всё", "А вот и всё", "Бабах", "Стоп игра"),
              new RandomText(
                  "Картошечка сгорела в чьих-то руках!",
                  "Горячая картошечка подпалила чей-то пукан!",
                  "Картошечка сгорела!",
                  "Горячая картошечка сгорела в чьих-то руках!",
                  "Чуствую запах хрустящего зада!",
                  "Прах сгоревшей картошечки укажет кто сегодня пидор...")));
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
