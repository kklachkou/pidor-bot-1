package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased.DateBasePidorFunnyAction;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.PrioritySelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.pidor.PidorOfDayService;
import by.kobyzau.tg.bot.pbot.service.pidor.PidorOfDayServiceFactory;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Primary
@Component
public class RandomNewPidorProcessor implements NewPidorProcessor {

  private final Selection<String> noPidorsMessage;

  private Selection<PidorFunnyAction> pidorFunnyActions;

  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private PidorOfDayServiceFactory pidorOfDayServiceFactory;
  @Autowired private PidorService pidorService;
  @Autowired private BotActionCollector botActionCollector;

  @Autowired private List<PidorFunnyAction> allPidorFunnyActions;

  @Autowired private List<DateBasePidorFunnyAction> dateBasePidorFunnyActions;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  public RandomNewPidorProcessor(Selection<String> noPidorsMessage) {
    this.noPidorsMessage = noPidorsMessage;
  }

  public RandomNewPidorProcessor() {
    this(new ConsistentSelection<>("Не вижу пидоров", "А где все педики?"));
  }

  public void setDateBasePidorFunnyActions(
      List<DateBasePidorFunnyAction> dateBasePidorFunnyActions) {
    this.dateBasePidorFunnyActions = dateBasePidorFunnyActions;
  }

  public void setPidorFunnyActions(Selection<PidorFunnyAction> pidorFunnyActions) {
    this.pidorFunnyActions = pidorFunnyActions;
  }

  @PostConstruct
  private void init() {
    pidorFunnyActions =
        new PrioritySelection<>(
            allPidorFunnyActions.stream()
                .map(p -> new Pair<>(p, p.getPriority()))
                .collect(Collectors.toList()));
  }

  @Override
  public void processNewDailyPidor(Message message) {
    long chatId = message.getChatId();
    List<Pidor> pidors = pidorService.getByChat(chatId);
    if (pidors.isEmpty()) {
      botActionCollector.text(chatId, new SimpleText(noPidorsMessage.next()));
      botActionCollector.sticker(chatId, StickerType.SAD.getRandom());
      return;
    }
    botActionCollector.typing(chatId);
    Pidor pidorOfTheDay = pidorOfDayServiceFactory.getService(PidorOfDayService.Type.SIMPLE).findPidorOfDay(chatId);
    saveDailyPidor(pidorOfTheDay, message.getFrom().getId());
    LocalDate now = DateUtil.now();
    Optional<DateBasePidorFunnyAction> dateBasePidorFunnyAction =
        dateBasePidorFunnyActions.stream().filter(a -> a.testDate(now)).findAny();
    if (dateBasePidorFunnyAction.isPresent()) {
      executor.execute(
          () -> dateBasePidorFunnyAction.get().processFunnyAction(chatId, pidorOfTheDay));
    } else {
      executor.execute(() -> pidorFunnyActions.next().processFunnyAction(chatId, pidorOfTheDay));
    }
  }

  private void saveDailyPidor(Pidor pidor, long callerId) {
    DailyPidor dailyPidor = new DailyPidor();
    dailyPidor.setChatId(pidor.getChatId());
    dailyPidor.setCaller(callerId);
    dailyPidor.setPlayerTgId(pidor.getTgId());
    dailyPidor.setLocalDate(DateUtil.now());
    dailyPidorRepository.create(dailyPidor);
  }
}
