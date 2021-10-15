package by.kobyzau.tg.bot.pbot.bots.election;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.bots.game.election.ElectionFinalizer;
import by.kobyzau.tg.bot.pbot.checker.BotActionAbstractTest;
import by.kobyzau.tg.bot.pbot.checker.BotTypeBotActionChecker;
import by.kobyzau.tg.bot.pbot.checker.TextBotActionChecker;
import by.kobyzau.tg.bot.pbot.games.election.stat.ElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.PidorFunnyAction;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.pidor.PidorOfDayService;
import by.kobyzau.tg.bot.pbot.service.pidor.PidorOfDayServiceFactory;
import by.kobyzau.tg.bot.pbot.tg.action.WaitBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ElectionFinalizerTest extends BotActionAbstractTest {

  @InjectMocks private ElectionFinalizer finalizer = new ElectionFinalizer();

  @Mock private ElectionService electionService;
  @Mock private PidorOfDayServiceFactory pidorOfDayServiceFactory;
  @Mock private PidorOfDayService electionPidorOfDayService;
  @Mock private DailyPidorRepository dailyPidorRepository;
  @Mock private BotService botService;
  @Mock private ElectionStatPrinter fullElectionStatPrinter;
  @Mock private UserArtifactService userArtifactService;

  @Mock private Selection<PidorFunnyAction> pidorFunnyActions;
  @Mock private PidorFunnyAction pidorFunnyAction;

  private static final long CHAT_ID = 123;

  @Before
  public void init() {
    finalizer.setPidorFunnyActions(pidorFunnyActions);
    doReturn(pidorFunnyAction).when(pidorFunnyActions).next();
    dailyPidorRepository.getAll().forEach(d -> dailyPidorRepository.delete(d.getId()));
    doReturn(electionPidorOfDayService)
        .when(pidorOfDayServiceFactory)
        .getService(PidorOfDayService.Type.ELECTION);
  }

  @After
  public void cleanArtifact() {
    verify(userArtifactService).clearUserArtifacts(CHAT_ID, ArtifactType.SILENCE);
    verify(userArtifactService).clearUserArtifacts(CHAT_ID, ArtifactType.RICOCHET);
    verify(userArtifactService).clearUserArtifacts(CHAT_ID, ArtifactType.BLINDNESS);
    verify(userArtifactService).clearUserArtifacts(CHAT_ID, ArtifactType.SUPER_VOTE);
  }

  @Test
  public void finalize_alreadyFinalized() {
    // given
    doReturn(Optional.of(new DailyPidor()))
        .when(dailyPidorRepository)
        .getByChatAndDate(CHAT_ID, DateUtil.now());

    // when
    finalizer.finalize(CHAT_ID);

    // then
    verify(fullElectionStatPrinter, times(0)).printInfo(CHAT_ID);
    checkNoAnyActions();
    verify(dailyPidorRepository, times(0)).create(any(DailyPidor.class));
    verify(botService, times(0)).unpinLastBotMessage(CHAT_ID);
    assertFunnyAction(0);
  }

  @Test
  public void finalize_finalize() {
    // given
    int numVotes = 5;
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(CHAT_ID, DateUtil.now());
    doReturn(numVotes).when(electionService).getNumVotes(CHAT_ID, DateUtil.now());
    Pidor pidorOfDay = new Pidor(1, CHAT_ID, "PidorOfDay");
    doReturn(pidorOfDay).when(electionPidorOfDayService).findPidorOfDay(CHAT_ID);

    // when
    finalizer.finalize(CHAT_ID);

    // then
    checkActions(
        new BotTypeBotActionChecker(WaitBotAction.class),
        new TextBotActionChecker(
            CHAT_ID, new ParametizedText("Сегодня я получил {0} голосов", new IntText(numVotes))),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new TextBotActionChecker(CHAT_ID, new SimpleText("И ситуация следующая:")),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new TextBotActionChecker(CHAT_ID, new SimpleText("Начнем выборы!")),
        new BotTypeBotActionChecker(WaitBotAction.class));
    verify(dailyPidorRepository).create(any(DailyPidor.class));
    verify(fullElectionStatPrinter).printInfo(CHAT_ID);
    verify(botService).unpinLastBotMessage(CHAT_ID);
    assertFunnyAction(1);
  }

  private void assertFunnyAction(int times) {
    verify(pidorFunnyAction, times(times)).processFunnyAction(eq(CHAT_ID), any());
  }
}
