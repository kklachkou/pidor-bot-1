package by.kobyzau.tg.bot.pbot.bots.game.election;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.games.election.stat.ElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.PidorFunnyAction;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.PrioritySelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElectionFinalizer {

  @Autowired private ElectionService electionService;

  @Autowired private PidorService pidorService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private BotService botService;
  @Autowired private ElectionStatPrinter fullElectionStatPrinter;

  @Autowired private List<PidorFunnyAction> allPidorFunnyActions;

  private Selection<PidorFunnyAction> pidorFunnyActions;

  @PostConstruct
  private void init() {
    pidorFunnyActions =
        new PrioritySelection<>(
            allPidorFunnyActions.stream()
                .map(p -> new Pair<>(p, p.getPriority()))
                .collect(Collectors.toList()));
  }

  public void finalize(long chatId) {
    LocalDate now = DateUtil.now();
    if (dailyPidorRepository.getByChatAndDate(chatId, now).isPresent()) {
      return;
    }
    botActionCollector.wait(chatId, ChatAction.TYPING);
    List<Pidor> pidors = pidorService.getByChat(chatId);
    int totalVotes = electionService.getNumVotes(chatId, now);
    botActionCollector.text(
        chatId, new ParametizedText("Сегодня я получил {0} голосов", new IntText(totalVotes)));
    botActionCollector.wait(chatId, ChatAction.TYPING);

    List<Pidor> pidorsToSelect = new ArrayList<>(pidors);
    for (Pidor pidor : pidors) {
      for (int i = 0; i < electionService.getNumVotes(chatId, now, pidor.getTgId()); i++) {
        pidorsToSelect.add(pidor);
      }
    }
    Pidor pidorOfDay = CollectionUtil.getRandomValue(pidorsToSelect);
    botActionCollector.text(chatId, new SimpleText("И ситуация следующая:"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    fullElectionStatPrinter.printInfo(chatId);
    botActionCollector.wait(chatId,5, ChatAction.TYPING);
    saveDailyPidor(pidorOfDay);
    botActionCollector.text(chatId, new RandomText("Начнем выборы!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    processFunnyAction(pidorOfDay);
  }

  private void processFunnyAction(Pidor pidor) {
    botService.unpinLastBotMessage(pidor.getChatId());
    pidorFunnyActions.next().processFunnyAction(pidor.getChatId(), pidor);
  }

  private void saveDailyPidor(Pidor pidor) {
    DailyPidor dailyPidor = new DailyPidor();
    dailyPidor.setChatId(pidor.getChatId());
    dailyPidor.setPlayerTgId(pidor.getTgId());
    dailyPidor.setLocalDate(DateUtil.now());
    dailyPidorRepository.create(dailyPidor);
  }

  public void setPidorFunnyActions(Selection<PidorFunnyAction> pidorFunnyActions) {
    this.pidorFunnyActions = pidorFunnyActions;
  }
}
