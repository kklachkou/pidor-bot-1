package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.ExcludeGameUserValue;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.exclude.ExcludeGameRepository;
import by.kobyzau.tg.bot.pbot.service.impl.ExcludeGameServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ExcludeGameServiceTest {

  private static final long CHAT_ID = 100;
  private static final LocalDate DATE = LocalDate.of(2021, 8, 23);
  @Mock private ExcludeGameRepository repository;
  @Mock private PidorService pidorService;
  @Mock private CalendarSchedule calendarSchedule;

  @InjectMocks private ExcludeGameService service = new ExcludeGameServiceImpl();

  @Test
  public void isExcludeGameDay_another() {
    // given
    doReturn(ScheduledItem.NONE).when(calendarSchedule).getItem(CHAT_ID, DATE);

    // when
    boolean excludeGameDay = service.isExcludeGameDay(CHAT_ID, DATE);

    // then
    assertFalse(excludeGameDay);
  }

  @Test
  public void isExcludeGameDay_excludeGame() {
    // given
    doReturn(ScheduledItem.EXCLUDE_GAME).when(calendarSchedule).getItem(CHAT_ID, DATE);

    // when
    boolean excludeGameDay = service.isExcludeGameDay(CHAT_ID, DATE);

    // then
    assertTrue(excludeGameDay);
  }

  @Test
  public void getExcludeGameUserValue_noValues() {
    // given
    long userId = 5;
    doReturn(
            Arrays.asList(
                getExcludeValue(userId - 1, DATE), getExcludeValue(userId, DATE.minusDays(1))))
        .when(repository)
        .getByChatId(CHAT_ID);

    // when
    Optional<ExcludeGameUserValue> excludeGameUserValue =
        service.getExcludeGameUserValue(CHAT_ID, userId, DATE);

    // then
    assertFalse(excludeGameUserValue.isPresent());
  }

  @Test
  public void getExcludeGameUserValue_hasValues() {
    // given
    long userId = 5;
    doReturn(
            Arrays.asList(
                getExcludeValue(userId - 1, DATE),
                getExcludeValue(userId, DATE),
                getExcludeValue(userId, DATE.minusDays(1))))
        .when(repository)
        .getByChatId(CHAT_ID);

    // when
    Optional<ExcludeGameUserValue> excludeGameUserValue =
        service.getExcludeGameUserValue(CHAT_ID, userId, DATE);

    // then
    assertTrue(excludeGameUserValue.isPresent());
    assertEquals(userId, excludeGameUserValue.get().getPlayerTgId());
  }

  @Test
  public void getExcludeGameUserValues_test() {
    // given
    doReturn(
            Arrays.asList(
                getExcludeValue(1, DATE.minusDays(1)),
                getExcludeValue(2, DATE),
                getExcludeValue(3, DATE),
                getExcludeValue(4, DATE.plusDays(1))))
        .when(repository)
        .getByChatId(CHAT_ID);

    // when
    List<ExcludeGameUserValue> excludeGameUserValue =
        service.getExcludeGameUserValues(CHAT_ID, DATE);

    // then
    assertEquals(
        Arrays.asList(getExcludeValue(2, DATE), getExcludeValue(3, DATE)), excludeGameUserValue);
  }

  @Test
  public void getNumPidorsToExclude_test() {
    // given
    Map<Integer, Integer> countToResult = new HashMap<>();
    countToResult.put(0, 0);
    countToResult.put(1, 0);
    countToResult.put(2, 1);
    countToResult.put(3, 2);
    countToResult.put(4, 3);
    countToResult.put(5, 4);
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
      Integer numPidorsToPlay = service.getNumPidorsToExclude(CHAT_ID);
      assertEquals(data.getValue(), numPidorsToPlay);
    }
  }

  private ExcludeGameUserValue getExcludeValue(long userId, LocalDate date) {
    return ExcludeGameUserValue.builder().playerTgId(userId).localDate(date).build();
  }
}
