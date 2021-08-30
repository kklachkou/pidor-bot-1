package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.CustomDailyUserData;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.custom.CustomDailyDataRepository;
import by.kobyzau.tg.bot.pbot.service.impl.ElectionServiceImpl;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static by.kobyzau.tg.bot.pbot.model.CustomDailyUserData.Type.ELECTION_VOTE;
import static by.kobyzau.tg.bot.pbot.model.CustomDailyUserData.Type.FUTURE_ACTION;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ElectionServiceTest {

  private static final long CHAT_ID = 100;
  private static final LocalDate DATE = DateUtil.now();
  @Mock private CalendarSchedule calendarSchedule;
  @Mock private CustomDailyDataRepository dailyDataRepository;
  @Mock private PidorService pidorService;

  @InjectMocks private ElectionService service = new ElectionServiceImpl();

  @Test
  public void isElectionDay_another() {
    // given
    doReturn(ScheduledItem.NONE).when(calendarSchedule).getItem(CHAT_ID, DATE);

    // when
    boolean electionDay = service.isElectionDay(CHAT_ID, DATE);

    // then
    assertFalse(electionDay);
  }

  @Test
  public void isElectionDay_election() {
    // given
    doReturn(ScheduledItem.ELECTION).when(calendarSchedule).getItem(CHAT_ID, DATE);

    // when
    boolean electionDay = service.isElectionDay(CHAT_ID, DATE);

    // then
    assertTrue(electionDay);
  }

  @Test
  public void canUserVote_noData() {
    // given
    long userId = 2;
    doReturn(Collections.emptyList()).when(dailyDataRepository).getByChatAndDate(CHAT_ID, DATE);

    // when
    boolean canVote = service.canUserVote(CHAT_ID, userId);

    // then
    assertTrue(canVote);
  }

  @Test
  public void canUserVote_noElectionData() {
    // given
    long userId = 2;
    doReturn(Arrays.asList(getData(userId), getData(userId + 1)))
        .when(dailyDataRepository)
        .getByChatAndDate(CHAT_ID, DATE);

    // when
    boolean canVote = service.canUserVote(CHAT_ID, userId);

    // then
    assertTrue(canVote);
  }

  @Test
  public void canUserVote_anotherUser() {
    // given
    long userId = 2;
    doReturn(Arrays.asList(getElectionData(userId - 1), getElectionData(userId + 1)))
        .when(dailyDataRepository)
        .getByChatAndDate(CHAT_ID, DATE);

    // when
    boolean canVote = service.canUserVote(CHAT_ID, userId);

    // then
    assertTrue(canVote);
  }

  @Test
  public void canUserVote_singleSameUser() {
    // given
    long userId = 2;
    doReturn(Collections.singletonList(getElectionData(userId)))
        .when(dailyDataRepository)
        .getByChatAndDate(CHAT_ID, DATE);

    // when
    boolean canVote = service.canUserVote(CHAT_ID, userId);

    // then
    assertFalse(canVote);
  }

  @Test
  public void canUserVote_hasSameUser() {
    // given
    long userId = 2;
    doReturn(
            Arrays.asList(
                getElectionData(userId - 1), getElectionData(userId), getElectionData(userId + 1)))
        .when(dailyDataRepository)
        .getByChatAndDate(CHAT_ID, DATE);

    // when
    boolean canVote = service.canUserVote(CHAT_ID, userId);

    // then
    assertFalse(canVote);
  }

  @Test
  public void getNumToVote_test() {
    Map<Integer, Integer> countToResult = new HashMap<>();
    countToResult.put(0, 0);
    countToResult.put(1, 1);
    countToResult.put(2, 2);
    countToResult.put(3, 3);
    countToResult.put(4, 4);
    countToResult.put(5, 5);
    countToResult.put(6, 5);
    countToResult.put(7, 5);
    countToResult.put(8, 6);
    countToResult.put(9, 7);
    countToResult.put(10, 8);
    countToResult.put(100, 80);

    for (Map.Entry<Integer, Integer> data : countToResult.entrySet()) {
      doReturn(
              IntStream.range(0, data.getKey())
                  .mapToObj(i -> new Pidor())
                  .collect(Collectors.toList()))
          .when(pidorService)
          .getByChat(CHAT_ID);
      Integer numPidorsToPlay = service.getNumToVote(CHAT_ID);
      assertEquals(data.getValue(), numPidorsToPlay);
    }
  }

  @Test
  public void getNumVotes_byChatAndDate() {
    // given
    doReturn(Arrays.asList(getData(1), getElectionData(2), getData(3)))
        .when(dailyDataRepository)
        .getByChatAndDate(CHAT_ID, DATE);

    // when
    int numVotes = service.getNumVotes(CHAT_ID, DATE);

    // then
    assertEquals(1, numVotes);
  }

  @Test
  public void getNumVotes_forUser() {
    // given
    long userId = 5;
    doReturn(
            Arrays.asList(
                getElectionDataVote(userId - 1),
                getElectionData(userId),
                getElectionDataVote(userId),
                getElectionDataVote(userId),
                getElectionDataVote(userId + 1)))
        .when(dailyDataRepository)
        .getByChatAndDate(CHAT_ID, DATE);

    // when
    int numVotes = service.getNumVotes(CHAT_ID, DATE, userId);

    // then
    assertEquals(2, numVotes);
  }

  private CustomDailyUserData getData(long userId) {
    return CustomDailyUserData.builder()
        .type(FUTURE_ACTION)
        .playerTgId(userId)
        .chatId(CHAT_ID)
        .build();
  }

  private CustomDailyUserData getElectionData(long userId) {
    return CustomDailyUserData.builder()
        .type(ELECTION_VOTE)
        .playerTgId(userId)
        .chatId(CHAT_ID)
        .build();
  }

  private CustomDailyUserData getElectionDataVote(long userId) {
    return CustomDailyUserData.builder()
        .type(ELECTION_VOTE)
        .data(String.valueOf(userId))
        .chatId(CHAT_ID)
        .build();
  }
}
