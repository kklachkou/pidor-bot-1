package by.kobyzau.tg.bot.pbot.bots.election;

import by.kobyzau.tg.bot.pbot.RangeMatcher;
import by.kobyzau.tg.bot.pbot.bots.game.election.ElectionFinalizer;
import by.kobyzau.tg.bot.pbot.checker.BotActionAbstractTest;
import by.kobyzau.tg.bot.pbot.checker.BotTypeBotActionChecker;
import by.kobyzau.tg.bot.pbot.checker.TextBotActionChecker;
import by.kobyzau.tg.bot.pbot.games.election.stat.impl.FullElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.PidorFunnyAction;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.HashMapPidorOfDayRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.WaitBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ElectionFinalizerTest extends BotActionAbstractTest {

  @InjectMocks private ElectionFinalizer finalizer = new ElectionFinalizer();

  @Mock private ElectionService electionService;
  @Mock private PidorService pidorService;
  @Spy private DailyPidorRepository dailyPidorRepository = new HashMapPidorOfDayRepository();

  @Mock private BotService botService;
  @Mock private FullElectionStatPrinter electionStatPrinter;

  @Mock private List<PidorFunnyAction> allPidorFunnyActions;
  @Mock private Selection<PidorFunnyAction> pidorFunnyActions;
  @Mock private PidorFunnyAction pidorFunnyAction;

  private final long chatId = 123;

  private static final int CHANCES_NUM_ATTEMPTS = 5_000;

  @Before
  public void init() {
    finalizer.setPidorFunnyActions(pidorFunnyActions);
    doReturn(pidorFunnyAction).when(pidorFunnyActions).next();
    dailyPidorRepository.getAll().forEach(d -> dailyPidorRepository.delete(d.getId()));
  }

  @Test
  public void finalize_alreadyFinalized() {
    // given
    doReturn(Optional.of(new DailyPidor()))
        .when(dailyPidorRepository)
        .getByChatAndDate(chatId, DateUtil.now());

    // when
    finalizer.finalize(chatId);

    // then
    checkNoAnyActions();
    assertSavedPidor(0);
    assertFunnyAction(0);
  }

  @Test
  public void finalize_finalize() {
    // given
    int numVotes = 5;
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(numVotes).when(electionService).getNumVotes(chatId, DateUtil.now());
    doReturn(Arrays.asList(getPidor(1), getPidor(2), getPidor(3)))
        .when(pidorService)
        .getByChat(chatId);

    // when
    finalizer.finalize(chatId);

    // then
    checkActions(
        new BotTypeBotActionChecker(WaitBotAction.class),
        new TextBotActionChecker(
            chatId, new ParametizedText("Сегодня я получил {0} голосов", new IntText(numVotes))),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new TextBotActionChecker(chatId, new SimpleText("И ситуация следующая:")),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new TextBotActionChecker(chatId, new SimpleText("Начнем выборы!")),
        new BotTypeBotActionChecker(WaitBotAction.class));
    assertSavedPidor(1);
    assertFunnyAction(1);
  }

  @Test
  public void finalize_chances() {
    // given
    List<Pair<Integer, Integer>> pidorIdsAndVotes =
        Arrays.asList(new Pair<>(1, 0), new Pair<>(2, 4), new Pair<>(3, 2));
    LocalDate now = DateUtil.now();
    List<Pidor> pidors = new ArrayList<>();
    int totalVotes = 0;
    for (Pair<Integer, Integer> pidorIdsAndVote : pidorIdsAndVotes) {
      pidors.add(getPidor(pidorIdsAndVote.getLeft()));
      doReturn(pidorIdsAndVote.getRight())
          .when(electionService)
          .getNumVotes(chatId, now, pidorIdsAndVote.getLeft());
      totalVotes += pidorIdsAndVote.getRight();
    }
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, now);
    doReturn(pidors).when(pidorService).getByChat(chatId);

    // when
    for (int i = 0; i < CHANCES_NUM_ATTEMPTS; i++) {
      finalizer.finalize(chatId);
    }

    // then
    assertEquals(CHANCES_NUM_ATTEMPTS, dailyPidorRepository.getAll().size());
    for (Pair<Integer, Integer> pidorIdsAndVote : pidorIdsAndVotes) {
      int expectedChance =
          100 * (pidorIdsAndVote.getRight() + 1) / (totalVotes + pidorIdsAndVotes.size());
      int realStatistic =
          100
              * (int)
                  dailyPidorRepository.getByChat(chatId).stream()
                      .filter(d -> d.getPlayerTgId() == pidorIdsAndVote.getLeft())
                      .count()
              / CHANCES_NUM_ATTEMPTS;
      assertRange(expectedChance, realStatistic);
    }
  }

  @Test
  public void finalize_noVotes() {
    // given
    LocalDate now = DateUtil.now();
    List<Pair<Integer, Integer>> pidorIdsAndVotes =
        Arrays.asList(new Pair<>(1, 0), new Pair<>(2, 0), new Pair<>(3, 0));
    List<Pidor> pidors = new ArrayList<>();
    int totalVotes = 0;
    for (Pair<Integer, Integer> pidorIdsAndVote : pidorIdsAndVotes) {
      pidors.add(getPidor(pidorIdsAndVote.getLeft()));
      doReturn(pidorIdsAndVote.getRight())
          .when(electionService)
          .getNumVotes(chatId, now, pidorIdsAndVote.getLeft());
      totalVotes += pidorIdsAndVote.getRight();
    }
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, now);
    doReturn(pidors).when(pidorService).getByChat(chatId);

    // when
    for (int i = 0; i < CHANCES_NUM_ATTEMPTS; i++) {
      finalizer.finalize(chatId);
    }

    // then
    assertEquals(0, totalVotes);
    assertEquals(CHANCES_NUM_ATTEMPTS, dailyPidorRepository.getAll().size());
    for (Pair<Integer, Integer> pidorIdsAndVote : pidorIdsAndVotes) {
      assertRange(
          100 / (pidorIdsAndVotes.size()),
          100
              * (int)
                  dailyPidorRepository.getByChat(chatId).stream()
                      .filter(d -> d.getPlayerTgId() == pidorIdsAndVote.getLeft())
                      .count()
              / CHANCES_NUM_ATTEMPTS);
    }
  }

  private void assertRange(int chance, int result) {
    assertThat(
        "Chance " + chance + ", result: " + result,
        result,
        new RangeMatcher(chance - 3, chance + 3));
  }

  private Pidor getPidor(int id) {
    return new Pidor(id, chatId, "Pidor" + id);
  }

  private void assertSavedPidor(int times) {
    verify(dailyPidorRepository, times(times)).create(any());
  }

  private void assertFunnyAction(int times) {
    verify(pidorFunnyAction, times(times)).processFunnyAction(eq(chatId), any());
  }
}
