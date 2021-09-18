package by.kobyzau.tg.bot.pbot.service.pidor.impl;

import by.kobyzau.tg.bot.pbot.RangeMatcher;
import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ElectionPidorOfDayServiceTest {

  @Rule
  public RepeatRule repeatRule = new RepeatRule();
  @Mock private PidorService pidorService;
  @Mock private UserArtifactService userArtifactService;
  @Mock private ElectionService electionService;

  @InjectMocks private PidorOfDayService service = new ElectionPidorOfDayService();

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
  }

  @Test
  @RepeatTest(times = 3)
  public void findPidorOfDay_sameVotes_noArtifacts() {
    // given
    doReturn(
            Collections.singletonList(
                UserArtifact.builder()
                    .userId(ID_2)
                    .artifactType(ArtifactType.SECOND_CHANCE)
                    .build()))
        .when(userArtifactService)
        .getUserArtifacts(CHAT_ID, DateUtil.now());
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_1);
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_2);
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_3);
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_4);
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
  public void findPidorOfDay_sameVotes_magnetArtifact() {
    // given
    doReturn(
            Collections.singletonList(
                UserArtifact.builder()
                    .userId(ID_2)
                    .artifactType(ArtifactType.PIDOR_MAGNET)
                    .build()))
        .when(userArtifactService)
        .getUserArtifacts(CHAT_ID, DateUtil.now());
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_1);
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_2);
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_3);
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_4);
    Map<Long, Integer> results = new HashMap<>();

    // when
    for (int i = 0; i < NUM_ITERATIONS; i++) {
      Pidor pidorOfDay = service.findPidorOfDay(CHAT_ID);
      int numWins = results.getOrDefault(pidorOfDay.getTgId(), 0);
      results.put(pidorOfDay.getTgId(), numWins + 1);
    }
    assertRange(23, 100 * results.getOrDefault(ID_1, 0) / NUM_ITERATIONS);
    assertRange(30, 100 * results.getOrDefault(ID_2, 0) / NUM_ITERATIONS);
    assertRange(23, 100 * results.getOrDefault(ID_3, 0) / NUM_ITERATIONS);
    assertRange(23, 100 * results.getOrDefault(ID_4, 0) / NUM_ITERATIONS);
  }

  @Test
  @RepeatTest(times = 3)
  public void findPidorOfDay_diffVotes_noArtifacts() {
    // given
    doReturn(
            Collections.singletonList(
                UserArtifact.builder()
                    .userId(ID_2)
                    .artifactType(ArtifactType.SECOND_CHANCE)
                    .build()))
        .when(userArtifactService)
        .getUserArtifacts(CHAT_ID, DateUtil.now());
    doReturn(1).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_1);
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_2);
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_3);
    doReturn(3).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_4);
    Map<Long, Integer> results = new HashMap<>();

    // when
    for (int i = 0; i < NUM_ITERATIONS; i++) {
      Pidor pidorOfDay = service.findPidorOfDay(CHAT_ID);
      int numWins = results.getOrDefault(pidorOfDay.getTgId(), 0);
      results.put(pidorOfDay.getTgId(), numWins + 1);
    }
    assertRange(16, 100 * results.getOrDefault(ID_1, 0) / NUM_ITERATIONS);
    assertRange(25, 100 * results.getOrDefault(ID_2, 0) / NUM_ITERATIONS);
    assertRange(25, 100 * results.getOrDefault(ID_3, 0) / NUM_ITERATIONS);
    assertRange(33, 100 * results.getOrDefault(ID_4, 0) / NUM_ITERATIONS);
  }

  @Test
  @RepeatTest(times = 3)
  public void findPidorOfDay_diffVotes_magnetArtifact() {
    // given
    doReturn(
            Collections.singletonList(
                UserArtifact.builder()
                    .userId(ID_2)
                    .artifactType(ArtifactType.PIDOR_MAGNET)
                    .build()))
        .when(userArtifactService)
        .getUserArtifacts(CHAT_ID, DateUtil.now());
    doReturn(1).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_1);
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_2);
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_3);
    doReturn(3).when(electionService).getNumVotes(CHAT_ID, DateUtil.now(), ID_4);
    Map<Long, Integer> results = new HashMap<>();

    // when
    for (int i = 0; i < NUM_ITERATIONS; i++) {
      Pidor pidorOfDay = service.findPidorOfDay(CHAT_ID);
      int numWins = results.getOrDefault(pidorOfDay.getTgId(), 0);
      results.put(pidorOfDay.getTgId(), numWins + 1);
    }
    assertRange(15, 100 * results.getOrDefault(ID_1, 0) / NUM_ITERATIONS);
    assertRange(31, 100 * results.getOrDefault(ID_2, 0) / NUM_ITERATIONS);
    assertRange(23, 100 * results.getOrDefault(ID_3, 0) / NUM_ITERATIONS);
    assertRange(31, 100 * results.getOrDefault(ID_4, 0) / NUM_ITERATIONS);
  }

  @Test
  public void getType_test() {
    //when
    PidorOfDayService.Type type = service.getType();

    //then
    Assert.assertEquals(PidorOfDayService.Type.ELECTION, type);
  }

  private void assertRange(int chance, int result) {
    MatcherAssert.assertThat(
        "Chance " + chance + ", result: " + result,
        result,
        new RangeMatcher(chance - ACCURACY, chance + ACCURACY));
  }
}
