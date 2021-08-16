package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorStatus;
import by.kobyzau.tg.bot.pbot.model.PidorYearlyStat;
import by.kobyzau.tg.bot.pbot.model.PidotStatusPosition;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.impl.PidorStatusServiceImpl;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class PidorStatusServiceTest {

  @InjectMocks private PidorStatusService service = new PidorStatusServiceImpl();

  @Mock private DailyPidorRepository dailyPidorRepository;
  @Mock private PidorService pidorService;
  @Mock private PidorYearlyStatService pidorYearlyStatService;

  private final long chatId = 123123;

  private final int year = 2000;

  @Before
  public void init() {
    doReturn(Optional.empty()).when(pidorYearlyStatService).getStat(anyLong(), anyLong(), anyInt());
  }

  @Test
  public void noPidors_emptyStat() {
    // given
    doReturn(Collections.emptyList()).when(pidorService).getByChat(chatId);
    doReturn(Collections.emptyList()).when(dailyPidorRepository).getByChat(chatId);

    // when
    PidorStatus pidorStatus = service.getPidorStatus(chatId, year);

    // then
    List<PidotStatusPosition> pidotStatusPositions = pidorStatus.getPidorStatusPositions();
    assertNotNull(pidotStatusPositions);
    assertTrue(CollectionUtil.isEmpty(pidotStatusPositions));
  }

  @Test
  public void hasPidors_noGames_noYearly_noPrimary() {
    // given
    List<Pidor> pidors = Arrays.asList(getPidor(1), getPidor(2));
    doReturn(pidors).when(pidorService).getByChat(chatId);
    doReturn(Collections.emptyList()).when(dailyPidorRepository).getByChat(chatId);

    // when
    PidorStatus pidorStatus = service.getPidorStatus(chatId, year);

    // then
    List<PidotStatusPosition> pidotStatusPositions = pidorStatus.getPidorStatusPositions();
    assertNotNull(pidotStatusPositions);
    assertEquals(1, pidotStatusPositions.size());
    PidotStatusPosition pidotStatusPosition = pidotStatusPositions.get(0);
    assertEquals(0, pidotStatusPosition.getNumWins());
    assertNull(pidotStatusPosition.getPrimaryPidor());
    assertPidorsSame("Secondary Pidors", pidors, pidotStatusPosition.getSecondaryPidors());
  }

  @Test
  public void hasPidors_oneWithStat() {
    // given
    Pidor pidor1No = getPidor(1);
    Pidor pidor2 = getPidor(2);
    Pidor pidor3No = getPidor(3);
    List<Pidor> pidors = Arrays.asList(pidor1No, pidor2, pidor3No);
    doReturn(pidors).when(pidorService).getByChat(chatId);
    doReturn(
            Arrays.asList(
                getDailyPidor(pidor2, LocalDate.of(year, 10, 10)),
                getDailyPidor(pidor2, LocalDate.of(year + 1, 9, 15)),
                getDailyPidor(pidor1No, LocalDate.of(year + 1, 8, 15)),
                getDailyPidor(pidor3No, LocalDate.of(year - 1, 7, 15)),
                getDailyPidor(pidor2, LocalDate.of(year, 10, 20))))
        .when(dailyPidorRepository)
        .getByChat(chatId);

    // when
    PidorStatus pidorStatus = service.getPidorStatus(chatId, year);

    // then
    List<PidotStatusPosition> pidotStatusPositions = pidorStatus.getPidorStatusPositions();
    assertNotNull(pidotStatusPositions);
    assertEquals(2, pidotStatusPositions.size());

    PidotStatusPosition firstPidorPosition = pidotStatusPositions.get(0);
    assertEquals(2, firstPidorPosition.getNumWins());
    assertEquals(pidor2, firstPidorPosition.getPrimaryPidor());
    assertPidorsSame(
        "first secondary", Collections.emptyList(), firstPidorPosition.getSecondaryPidors());

    PidotStatusPosition secondPidorPosition = pidotStatusPositions.get(1);
    assertEquals(0, secondPidorPosition.getNumWins());
    assertNull(secondPidorPosition.getPrimaryPidor());
    assertPidorsSame(
        "second secondary",
        Arrays.asList(pidor1No, pidor3No),
        secondPidorPosition.getSecondaryPidors());
  }

  @Test
  public void hasPidors_oneWithStatByYearly() {
    // given
    Pidor pidor1No = getPidor(1);
    Pidor pidor2 = getPidor(2);
    Pidor pidor3No = getPidor(3);
    List<Pidor> pidors = Arrays.asList(pidor1No, pidor2, pidor3No);
    doReturn(pidors).when(pidorService).getByChat(chatId);
    doReturn(
            Arrays.asList(
                getDailyPidor(pidor2, LocalDate.of(year + 1, 9, 15)),
                getDailyPidor(pidor1No, LocalDate.of(year + 1, 8, 15)),
                getDailyPidor(pidor3No, LocalDate.of(year - 1, 7, 15)),
                getDailyPidor(pidor2, LocalDate.of(year, 10, 20))))
        .when(dailyPidorRepository)
        .getByChat(chatId);
    doReturn(Optional.of(getStat(2, 5, LocalDate.of(year, 10, 4))))
        .when(pidorYearlyStatService)
        .getStat(chatId, 2, year);

    // when
    PidorStatus pidorStatus = service.getPidorStatus(chatId, year);

    // then
    List<PidotStatusPosition> pidotStatusPositions = pidorStatus.getPidorStatusPositions();
    assertNotNull(pidotStatusPositions);
    assertEquals(2, pidotStatusPositions.size());

    PidotStatusPosition firstPidorPosition = pidotStatusPositions.get(0);
    assertEquals(5, firstPidorPosition.getNumWins());
    assertEquals(pidor2, firstPidorPosition.getPrimaryPidor());
    assertPidorsSame(
        "first secondary", Collections.emptyList(), firstPidorPosition.getSecondaryPidors());

    PidotStatusPosition secondPidorPosition = pidotStatusPositions.get(1);
    assertEquals(0, secondPidorPosition.getNumWins());
    assertNull(secondPidorPosition.getPrimaryPidor());
    assertPidorsSame(
        "second secondary",
        Arrays.asList(pidor1No, pidor3No),
        secondPidorPosition.getSecondaryPidors());
  }

  @Test
  public void hasPidors_threeWithStat_noSame() {
    // given
    Pidor pidor1No = getPidor(1);
    Pidor pidor2_1 = getPidor(2); // 1
    Pidor pidor3_3 = getPidor(3); // 3
    Pidor pidor4_2 = getPidor(4); // 2
    Pidor pidor5No = getPidor(5);
    List<Pidor> pidors = Arrays.asList(pidor1No, pidor2_1, pidor3_3, pidor4_2, pidor5No);
    doReturn(pidors).when(pidorService).getByChat(chatId);
    doReturn(
            Arrays.asList(
                getDailyPidor(pidor1No, LocalDate.of(year + 1, 10, 10)),
                getDailyPidor(pidor2_1, LocalDate.of(year, 9, 15)),
                getDailyPidor(pidor3_3, LocalDate.of(year, 8, 1)),
                getDailyPidor(pidor3_3, LocalDate.of(year, 8, 2)),
                getDailyPidor(pidor3_3, LocalDate.of(year, 7, 3)),
                getDailyPidor(pidor4_2, LocalDate.of(year, 7, 4)),
                getDailyPidor(pidor4_2, LocalDate.of(year, 7, 5)),
                getDailyPidor(pidor5No, LocalDate.of(year + 2, 10, 20))))
        .when(dailyPidorRepository)
        .getByChat(chatId);

    // when
    PidorStatus pidorStatus = service.getPidorStatus(chatId, year);

    // then
    List<PidotStatusPosition> pidotStatusPositions = pidorStatus.getPidorStatusPositions();
    assertNotNull(pidotStatusPositions);
    assertEquals(4, pidotStatusPositions.size());

    PidotStatusPosition pidorPosition1 = pidotStatusPositions.get(0);
    assertEquals(3, pidorPosition1.getNumWins());
    assertEquals(pidor3_3, pidorPosition1.getPrimaryPidor());
    assertPidorsSame(
        "first secondary", Collections.emptyList(), pidorPosition1.getSecondaryPidors());

    PidotStatusPosition pidorPosition2 = pidotStatusPositions.get(1);
    assertEquals(2, pidorPosition2.getNumWins());
    assertEquals(pidor4_2, pidorPosition2.getPrimaryPidor());
    assertPidorsSame(
        "second secondary", Collections.emptyList(), pidorPosition2.getSecondaryPidors());

    PidotStatusPosition pidorPosition3 = pidotStatusPositions.get(2);
    assertEquals(1, pidorPosition3.getNumWins());
    assertEquals(pidor2_1, pidorPosition3.getPrimaryPidor());
    assertPidorsSame(
        "third secondary", Collections.emptyList(), pidorPosition3.getSecondaryPidors());

    PidotStatusPosition pidorPositior4 = pidotStatusPositions.get(3);
    assertEquals(0, pidorPositior4.getNumWins());
    assertNull(pidorPositior4.getPrimaryPidor());
    assertPidorsSame(
        "last secondary", Arrays.asList(pidor1No, pidor5No), pidorPositior4.getSecondaryPidors());
  }

  @Test
  public void hasPidors_threeWithStatWithYearly_noSame() {
    // given
    Pidor pidor1No = getPidor(1);
    Pidor pidor2_1 = getPidor(2); // 1
    Pidor pidor3_3 = getPidor(3); // 3
    Pidor pidor4_2 = getPidor(4); // 2
    Pidor pidor5No = getPidor(5);
    List<Pidor> pidors = Arrays.asList(pidor1No, pidor2_1, pidor3_3, pidor4_2, pidor5No);
    doReturn(pidors).when(pidorService).getByChat(chatId);
    doReturn(
            Arrays.asList(
                getDailyPidor(pidor1No, LocalDate.of(year + 1, 10, 10)),
                getDailyPidor(pidor2_1, LocalDate.of(year, 9, 15)),
                getDailyPidor(pidor3_3, LocalDate.of(year, 8, 1)),
                getDailyPidor(pidor3_3, LocalDate.of(year, 8, 2)),
                getDailyPidor(pidor3_3, LocalDate.of(year, 7, 3)),
                getDailyPidor(pidor5No, LocalDate.of(year + 2, 10, 20))))
        .when(dailyPidorRepository)
        .getByChat(chatId);
    doReturn(Optional.of(getStat(4, 2, LocalDate.of(year, 7, 4))))
        .when(pidorYearlyStatService)
        .getStat(chatId, 4, year);

    // when
    PidorStatus pidorStatus = service.getPidorStatus(chatId, year);

    // then
    List<PidotStatusPosition> pidotStatusPositions = pidorStatus.getPidorStatusPositions();
    assertNotNull(pidotStatusPositions);
    assertEquals(4, pidotStatusPositions.size());

    PidotStatusPosition pidorPosition1 = pidotStatusPositions.get(0);
    assertEquals(3, pidorPosition1.getNumWins());
    assertEquals(pidor3_3, pidorPosition1.getPrimaryPidor());
    assertPidorsSame(
        "first secondary", Collections.emptyList(), pidorPosition1.getSecondaryPidors());

    PidotStatusPosition pidorPosition2 = pidotStatusPositions.get(1);
    assertEquals(2, pidorPosition2.getNumWins());
    assertEquals(pidor4_2, pidorPosition2.getPrimaryPidor());
    assertPidorsSame(
        "second secondary", Collections.emptyList(), pidorPosition2.getSecondaryPidors());

    PidotStatusPosition pidorPosition3 = pidotStatusPositions.get(2);
    assertEquals(1, pidorPosition3.getNumWins());
    assertEquals(pidor2_1, pidorPosition3.getPrimaryPidor());
    assertPidorsSame(
        "third secondary", Collections.emptyList(), pidorPosition3.getSecondaryPidors());

    PidotStatusPosition pidorPositior4 = pidotStatusPositions.get(3);
    assertEquals(0, pidorPositior4.getNumWins());
    assertNull(pidorPositior4.getPrimaryPidor());
    assertPidorsSame(
        "last secondary", Arrays.asList(pidor1No, pidor5No), pidorPositior4.getSecondaryPidors());
  }

  @Test
  public void hasPidors_twoWithStat_hasPrimary() {
    // given
    Pidor pidor1No = getPidor(1);
    Pidor pidor2_1 = getPidor(2); // 1
    Pidor pidor3_2 = getPidor(3); // 2
    Pidor pidor4_2 = getPidor(4); // 2
    Pidor pidor5No = getPidor(5);
    List<Pidor> pidors = Arrays.asList(pidor1No, pidor2_1, pidor3_2, pidor4_2, pidor5No);
    doReturn(pidors).when(pidorService).getByChat(chatId);
    doReturn(
            Arrays.asList(
                    getDailyPidor(pidor1No, LocalDate.of(year + 1, 10, 10)),
                    getDailyPidor(pidor2_1, LocalDate.of(year, 9, 15)),
                    getDailyPidor(pidor3_2, LocalDate.of(year, 1, 19)),
                    getDailyPidor(pidor4_2, LocalDate.of(year, 1, 21)),
                    getDailyPidor(pidor3_2, LocalDate.of(year, 1, 20)),
                    getDailyPidor(pidor4_2, LocalDate.of(year, 1, 6)),
                    getDailyPidor(pidor5No, LocalDate.of(year + 2, 10, 20))))
            .when(dailyPidorRepository)
            .getByChat(chatId);

    // when
    PidorStatus pidorStatus = service.getPidorStatus(chatId, year);

    // then
    List<PidotStatusPosition> pidotStatusPositions = pidorStatus.getPidorStatusPositions();
    assertNotNull(pidotStatusPositions);
    assertEquals(3, pidotStatusPositions.size());

    PidotStatusPosition pidorPosition1 = pidotStatusPositions.get(0);
    assertEquals(2, pidorPosition1.getNumWins());
    assertEquals(pidor4_2, pidorPosition1.getPrimaryPidor());
    assertPidorsSame(
            "first secondary",
            Collections.singletonList(pidor3_2),
            pidorPosition1.getSecondaryPidors());

    PidotStatusPosition pidorPosition3 = pidotStatusPositions.get(1);
    assertEquals(1, pidorPosition3.getNumWins());
    assertEquals(pidor2_1, pidorPosition3.getPrimaryPidor());
    assertPidorsSame(
            "third secondary", Collections.emptyList(), pidorPosition3.getSecondaryPidors());

    PidotStatusPosition pidorPositior4 = pidotStatusPositions.get(2);
    assertEquals(0, pidorPositior4.getNumWins());
    assertNull(pidorPositior4.getPrimaryPidor());
    assertPidorsSame(
            "last secondary", Arrays.asList(pidor1No, pidor5No), pidorPositior4.getSecondaryPidors());
  }


  @Test
  public void hasPidors_twoWithStatWithYearly_hasPrimary() {
    // given
    Pidor pidor1No = getPidor(1);
    Pidor pidor2_1 = getPidor(2); // 1
    Pidor pidor3_2 = getPidor(3); // 2
    Pidor pidor4_2 = getPidor(4); // 2
    Pidor pidor5No = getPidor(5);
    List<Pidor> pidors = Arrays.asList(pidor1No, pidor2_1, pidor3_2, pidor4_2, pidor5No);
    doReturn(pidors).when(pidorService).getByChat(chatId);
    doReturn(
            Arrays.asList(
                    getDailyPidor(pidor1No, LocalDate.of(year + 1, 10, 10)),
                    getDailyPidor(pidor2_1, LocalDate.of(year, 9, 15)),
                    getDailyPidor(pidor3_2, LocalDate.of(year, 1, 19)),
                    getDailyPidor(pidor3_2, LocalDate.of(year, 1, 20)),
                    getDailyPidor(pidor5No, LocalDate.of(year + 2, 10, 20))))
            .when(dailyPidorRepository)
            .getByChat(chatId);
    doReturn(Optional.of(getStat(4, 2, LocalDate.of(year, 1, 21))))
            .when(pidorYearlyStatService)
            .getStat(chatId, 4, year);
    // when
    PidorStatus pidorStatus = service.getPidorStatus(chatId, year);

    // then
    List<PidotStatusPosition> pidotStatusPositions = pidorStatus.getPidorStatusPositions();
    assertNotNull(pidotStatusPositions);
    assertEquals(3, pidotStatusPositions.size());

    PidotStatusPosition pidorPosition1 = pidotStatusPositions.get(0);
    assertEquals(2, pidorPosition1.getNumWins());
    assertEquals(pidor4_2, pidorPosition1.getPrimaryPidor());
    assertPidorsSame(
            "first secondary",
            Collections.singletonList(pidor3_2),
            pidorPosition1.getSecondaryPidors());

    PidotStatusPosition pidorPosition3 = pidotStatusPositions.get(1);
    assertEquals(1, pidorPosition3.getNumWins());
    assertEquals(pidor2_1, pidorPosition3.getPrimaryPidor());
    assertPidorsSame(
            "third secondary", Collections.emptyList(), pidorPosition3.getSecondaryPidors());

    PidotStatusPosition pidorPositior4 = pidotStatusPositions.get(2);
    assertEquals(0, pidorPositior4.getNumWins());
    assertNull(pidorPositior4.getPrimaryPidor());
    assertPidorsSame(
            "last secondary", Arrays.asList(pidor1No, pidor5No), pidorPositior4.getSecondaryPidors());
  }

  private void assertPidorsSame(String message, List<Pidor> expected, List<Pidor> result) {
    assertNotNull(message, expected);
    assertNotNull(message, result);
    assertEquals(message, expected.size(), result.size());
    assertEquals(message, new HashSet<>(expected), new HashSet<>(result));
  }

  private DailyPidor getDailyPidor(Pidor pidor, LocalDate localDate) {
    DailyPidor dailyPidor = new DailyPidor();
    dailyPidor.setId(pidor.getId() * 10000);
    dailyPidor.setPlayerTgId(pidor.getTgId());
    dailyPidor.setChatId(chatId);
    dailyPidor.setLocalDate(localDate);
    return dailyPidor;
  }

  private Pidor getPidor(int id) {
    Pidor pidor = new Pidor();
    pidor.setId(id * 1000);
    pidor.setChatId(chatId);
    pidor.setTgId(id);
    pidor.setFullName("" + id);
    pidor.setUsername("" + id);
    return pidor;
  }

  private PidorYearlyStat getStat(int id, int count, LocalDate lastDate) {
    PidorYearlyStat stat = new PidorYearlyStat();
    stat.setChatId(chatId);
    stat.setCount(count);
    stat.setLastDate(lastDate);
    stat.setPlayerTgId(id);
    return stat;
  }
}
