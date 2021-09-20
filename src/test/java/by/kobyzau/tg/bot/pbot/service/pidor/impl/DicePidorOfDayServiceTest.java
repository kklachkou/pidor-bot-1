package by.kobyzau.tg.bot.pbot.service.pidor.impl;

import by.kobyzau.tg.bot.pbot.RangeMatcher;
import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameResult;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorDice;
import by.kobyzau.tg.bot.pbot.service.DiceService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.pidor.PidorOfDayService;
import by.kobyzau.tg.bot.pbot.test.repeat.RepeatRule;
import by.kobyzau.tg.bot.pbot.test.repeat.RepeatTest;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class DicePidorOfDayServiceTest {

  @Rule public RepeatRule repeatRule = new RepeatRule();
  @Mock private PidorService pidorService;
  @Mock private UserArtifactService userArtifactService;
  @Mock private DiceService diceService;
  @Mock private EmojiGame emojiGame;

  @InjectMocks private PidorOfDayService service = new DicePidorOfDayService();

  private static final long CHAT_ID = 1001;
  private static final long ID_1 = 1;
  private static final long ID_2 = 2;
  private static final long ID_3 = 3;
  private static final long ID_4 = 4;
  private static final int NUM_ITERATIONS = 2000;
  private static final int ACCURACY = 3;

  @Before
  public void init() {
    doReturn(
            Arrays.asList(
                new Pidor(ID_1, CHAT_ID, "Pidor1"),
                new Pidor(ID_2, CHAT_ID, "Pidor2"),
                new Pidor(ID_3, CHAT_ID, "Pidor3"),
                new Pidor(ID_4, CHAT_ID, "Pidor4")))
        .when(pidorService)
        .getByChat(CHAT_ID);
    doReturn(Optional.of(emojiGame)).when(diceService).getGame(CHAT_ID, DateUtil.now());
    doReturn(Optional.of(PidorDice.builder().value((int) ID_1).build()))
        .when(diceService)
        .getUserDice(CHAT_ID, ID_1, DateUtil.now());
    doReturn(Optional.of(PidorDice.builder().value((int) ID_2).build()))
        .when(diceService)
        .getUserDice(CHAT_ID, ID_2, DateUtil.now());
    doReturn(Optional.of(PidorDice.builder().value((int) ID_3).build()))
        .when(diceService)
        .getUserDice(CHAT_ID, ID_3, DateUtil.now());
    doReturn(Optional.of(PidorDice.builder().value((int) ID_4).build()))
        .when(diceService)
        .getUserDice(CHAT_ID, ID_4, DateUtil.now());
  }

  @Test
  @RepeatTest(times = 3)
  public void findPidorOfDay_sameResult_noArtifact() {
    // given
    doReturn(
            Collections.singletonList(
                UserArtifact.builder()
                    .userId(ID_2)
                    .artifactType(ArtifactType.SECOND_CHANCE)
                    .build()))
        .when(userArtifactService)
        .getUserArtifacts(CHAT_ID);
    doReturn(EmojiGameResult.LOSE).when(emojiGame).getResult(eq(CHAT_ID), anyInt());
    Map<Long, Integer> results = new HashMap<>();

    // when
    for (int i = 0; i < NUM_ITERATIONS; i++) {
      Pidor pidorOfDay = service.findPidorOfDay(CHAT_ID);
      int numWins = results.getOrDefault(pidorOfDay.getTgId(), 0);
      results.put(pidorOfDay.getTgId(), numWins + 1);
    }
    assertRange(25, 100 * results.getOrDefault(ID_1, 0) / NUM_ITERATIONS);
    assertRange(25, 100 * results.getOrDefault(ID_2, 0) / NUM_ITERATIONS);
    assertRange(25, 100 * results.getOrDefault(ID_3, 0) / NUM_ITERATIONS);
    assertRange(25, 100 * results.getOrDefault(ID_4, 0) / NUM_ITERATIONS);
  }

  @Test
  @RepeatTest(times = 3)
  public void findPidorOfDay_sameResult_withArtifact() {
    // given
    doReturn(
            Collections.singletonList(
                UserArtifact.builder()
                    .userId(ID_2)
                    .artifactType(ArtifactType.PIDOR_MAGNET)
                    .build()))
        .when(userArtifactService)
        .getUserArtifacts(CHAT_ID);
    doReturn(EmojiGameResult.LOSE).when(emojiGame).getResult(eq(CHAT_ID), anyInt());
    Map<Long, Integer> results = new HashMap<>();

    // when
    for (int i = 0; i < NUM_ITERATIONS; i++) {
      Pidor pidorOfDay = service.findPidorOfDay(CHAT_ID);
      int numWins = results.getOrDefault(pidorOfDay.getTgId(), 0);
      results.put(pidorOfDay.getTgId(), numWins + 1);
    }
    assertRange(20, 100 * results.getOrDefault(ID_1, 0) / NUM_ITERATIONS);
    assertRange(40, 100 * results.getOrDefault(ID_2, 0) / NUM_ITERATIONS);
    assertRange(20, 100 * results.getOrDefault(ID_3, 0) / NUM_ITERATIONS);
    assertRange(20, 100 * results.getOrDefault(ID_4, 0) / NUM_ITERATIONS);
  }

  @Test
  @RepeatTest(times = 3)
  public void findPidorOfDay_hasWinner_noArtifact() {
    // given
    doReturn(
            Collections.singletonList(
                UserArtifact.builder()
                    .userId(ID_2)
                    .artifactType(ArtifactType.SECOND_CHANCE)
                    .build()))
        .when(userArtifactService)
        .getUserArtifacts(CHAT_ID);
    doReturn(EmojiGameResult.LOSE).when(emojiGame).getResult(eq(CHAT_ID), anyInt());
    doReturn(EmojiGameResult.WIN).when(emojiGame).getResult(CHAT_ID, (int) ID_3);
    Map<Long, Integer> results = new HashMap<>();

    // when
    for (int i = 0; i < NUM_ITERATIONS; i++) {
      Pidor pidorOfDay = service.findPidorOfDay(CHAT_ID);
      int numWins = results.getOrDefault(pidorOfDay.getTgId(), 0);
      results.put(pidorOfDay.getTgId(), numWins + 1);
    }
    assertRange(33, 100 * results.getOrDefault(ID_1, 0) / NUM_ITERATIONS);
    assertRange(33, 100 * results.getOrDefault(ID_2, 0) / NUM_ITERATIONS);
    assertRange(0, 100 * results.getOrDefault(ID_3, 0) / NUM_ITERATIONS);
    assertRange(33, 100 * results.getOrDefault(ID_4, 0) / NUM_ITERATIONS);
  }

  @Test
  @RepeatTest(times = 3)
  public void findPidorOfDay_hasWinner_withArtifact_matched() {
    // given
    doReturn(
            Collections.singletonList(
                UserArtifact.builder()
                    .userId(ID_2)
                    .artifactType(ArtifactType.PIDOR_MAGNET)
                    .build()))
        .when(userArtifactService)
        .getUserArtifacts(CHAT_ID);
    doReturn(EmojiGameResult.LOSE).when(emojiGame).getResult(eq(CHAT_ID), anyInt());
    doReturn(EmojiGameResult.WIN).when(emojiGame).getResult(CHAT_ID, (int) ID_2);
    Map<Long, Integer> results = new HashMap<>();

    // when
    for (int i = 0; i < NUM_ITERATIONS; i++) {
      Pidor pidorOfDay = service.findPidorOfDay(CHAT_ID);
      int numWins = results.getOrDefault(pidorOfDay.getTgId(), 0);
      results.put(pidorOfDay.getTgId(), numWins + 1);
    }
    assertRange(33, 100 * results.getOrDefault(ID_1, 0) / NUM_ITERATIONS);
    assertRange(0, 100 * results.getOrDefault(ID_2, 0) / NUM_ITERATIONS);
    assertRange(33, 100 * results.getOrDefault(ID_3, 0) / NUM_ITERATIONS);
    assertRange(33, 100 * results.getOrDefault(ID_4, 0) / NUM_ITERATIONS);
  }

  @Test
  @RepeatTest(times = 3)
  public void findPidorOfDay_hasWinner_withArtifact_notMatched() {
    // given
    doReturn(
            Collections.singletonList(
                UserArtifact.builder()
                    .userId(ID_2)
                    .artifactType(ArtifactType.PIDOR_MAGNET)
                    .build()))
        .when(userArtifactService)
        .getUserArtifacts(CHAT_ID);
    doReturn(EmojiGameResult.LOSE).when(emojiGame).getResult(eq(CHAT_ID), anyInt());
    doReturn(EmojiGameResult.WIN).when(emojiGame).getResult(CHAT_ID, (int) ID_3);
    Map<Long, Integer> results = new HashMap<>();

    // when
    for (int i = 0; i < NUM_ITERATIONS; i++) {
      Pidor pidorOfDay = service.findPidorOfDay(CHAT_ID);
      int numWins = results.getOrDefault(pidorOfDay.getTgId(), 0);
      results.put(pidorOfDay.getTgId(), numWins + 1);
    }
    assertRange(25, 100 * results.getOrDefault(ID_1, 0) / NUM_ITERATIONS);
    assertRange(50, 100 * results.getOrDefault(ID_2, 0) / NUM_ITERATIONS);
    assertRange(0, 100 * results.getOrDefault(ID_3, 0) / NUM_ITERATIONS);
    assertRange(25, 100 * results.getOrDefault(ID_4, 0) / NUM_ITERATIONS);
  }

  @Test
  @RepeatTest(times = 3)
  public void findPidorOfDay_hasNone_noArtifact() {
    // given
    doReturn(
            Collections.singletonList(
                UserArtifact.builder()
                    .userId(ID_2)
                    .artifactType(ArtifactType.SECOND_CHANCE)
                    .build()))
        .when(userArtifactService)
        .getUserArtifacts(CHAT_ID);
    doReturn(EmojiGameResult.LOSE).when(emojiGame).getResult(eq(CHAT_ID), anyInt());
    doReturn(EmojiGameResult.NONE).when(emojiGame).getResult(CHAT_ID, (int) ID_3);
    Map<Long, Integer> results = new HashMap<>();

    // when
    for (int i = 0; i < NUM_ITERATIONS; i++) {
      Pidor pidorOfDay = service.findPidorOfDay(CHAT_ID);
      int numWins = results.getOrDefault(pidorOfDay.getTgId(), 0);
      results.put(pidorOfDay.getTgId(), numWins + 1);
    }
    assertRange(20, 100 * results.getOrDefault(ID_1, 0) / NUM_ITERATIONS);
    assertRange(20, 100 * results.getOrDefault(ID_2, 0) / NUM_ITERATIONS);
    assertRange(40, 100 * results.getOrDefault(ID_3, 0) / NUM_ITERATIONS);
    assertRange(20, 100 * results.getOrDefault(ID_4, 0) / NUM_ITERATIONS);
  }

  @Test
  @RepeatTest(times = 3)
  public void findPidorOfDay_hasNone_withArtifact_matched() {
    // given
    doReturn(
            Collections.singletonList(
                UserArtifact.builder()
                    .userId(ID_2)
                    .artifactType(ArtifactType.PIDOR_MAGNET)
                    .build()))
        .when(userArtifactService)
        .getUserArtifacts(CHAT_ID);
    doReturn(EmojiGameResult.LOSE).when(emojiGame).getResult(eq(CHAT_ID), anyInt());
    doReturn(EmojiGameResult.NONE).when(emojiGame).getResult(CHAT_ID, (int) ID_2);
    Map<Long, Integer> results = new HashMap<>();

    // when
    for (int i = 0; i < NUM_ITERATIONS; i++) {
      Pidor pidorOfDay = service.findPidorOfDay(CHAT_ID);
      int numWins = results.getOrDefault(pidorOfDay.getTgId(), 0);
      results.put(pidorOfDay.getTgId(), numWins + 1);
    }
    assertRange(16, 100 * results.getOrDefault(ID_1, 0) / NUM_ITERATIONS);
    assertRange(50, 100 * results.getOrDefault(ID_2, 0) / NUM_ITERATIONS);
    assertRange(16, 100 * results.getOrDefault(ID_3, 0) / NUM_ITERATIONS);
    assertRange(16, 100 * results.getOrDefault(ID_4, 0) / NUM_ITERATIONS);
  }

  @Test
  @RepeatTest(times = 3)
  public void findPidorOfDay_hasNone_withArtifact_notMatched() {
    // given
    doReturn(
            Collections.singletonList(
                UserArtifact.builder()
                    .userId(ID_2)
                    .artifactType(ArtifactType.PIDOR_MAGNET)
                    .build()))
        .when(userArtifactService)
        .getUserArtifacts(CHAT_ID);
    doReturn(EmojiGameResult.LOSE).when(emojiGame).getResult(eq(CHAT_ID), anyInt());
    doReturn(EmojiGameResult.NONE).when(emojiGame).getResult(CHAT_ID, (int) ID_3);
    Map<Long, Integer> results = new HashMap<>();

    // when
    for (int i = 0; i < NUM_ITERATIONS; i++) {
      Pidor pidorOfDay = service.findPidorOfDay(CHAT_ID);
      int numWins = results.getOrDefault(pidorOfDay.getTgId(), 0);
      results.put(pidorOfDay.getTgId(), numWins + 1);
    }
    assertRange(16, 100 * results.getOrDefault(ID_1, 0) / NUM_ITERATIONS);
    assertRange(33, 100 * results.getOrDefault(ID_2, 0) / NUM_ITERATIONS);
    assertRange(33, 100 * results.getOrDefault(ID_3, 0) / NUM_ITERATIONS);
    assertRange(16, 100 * results.getOrDefault(ID_4, 0) / NUM_ITERATIONS);
  }

  @Test
  public void getType_test() {
    //when
    PidorOfDayService.Type type = service.getType();

    //then
    Assert.assertEquals(PidorOfDayService.Type.DICE, type);
  }

  private void assertRange(int chance, int result) {
    MatcherAssert.assertThat(
        "Chance " + chance + ", result: " + result,
        result,
        new RangeMatcher(chance - ACCURACY, chance + ACCURACY));
  }
}
