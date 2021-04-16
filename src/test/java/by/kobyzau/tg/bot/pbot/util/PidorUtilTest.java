package by.kobyzau.tg.bot.pbot.util;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class PidorUtilTest {

  @Test
  public void getTopPidorTgId_emptyList_throwError() {
    // given
    List<DailyPidor> dailyPidors = Collections.emptyList();

    // when
    Optional<Long> topPidor = PidorUtil.getTopPidorTgId(dailyPidors);

    // then
    assertFalse(topPidor.isPresent());
  }

  @Test
  public void getTopPidorTgId_oneHasTopWins() {
    // given
    final int year = 2019;
    final int month = 12;
    final long player1 = 1;
    final long player2 = 2;
    final long player3 = 3;
    final long player4 = 4;
    int day = 1;
    List<DailyPidor> dailyPidors =
        Arrays.asList(
            getDailyPidor(player2, LocalDate.of(year, month, day++)),
            getDailyPidor(player1, LocalDate.of(year, month, day++)),
            getDailyPidor(player2, LocalDate.of(year, month, day++)),
            getDailyPidor(player1, LocalDate.of(year, month, day++)),
            getDailyPidor(player3, LocalDate.of(year, month, day++)),
            getDailyPidor(player3, LocalDate.of(year, month, day++)),
            getDailyPidor(player1, LocalDate.of(year, month, day++)),
            getDailyPidor(player4, LocalDate.of(year, month, day)));

    // when
    Optional<Long> topPidor = PidorUtil.getTopPidorTgId(dailyPidors);

    // then
    assertTrue(topPidor.isPresent());
    assertEquals(Long.valueOf(player1), topPidor.get());
  }

  @Test
  public void getTopPidorTgId_severalHaveTopWins() {
    // given
    final int year = 2019;
    final int month = 12;
    final int player1 = 1;
    final int player2 = 2;
    final int player3 = 3;
    final int player4 = 4;
    int day = 1;
    List<DailyPidor> dailyPidors =
        Arrays.asList(
            getDailyPidor(player2, LocalDate.of(year, month, day++)),
            getDailyPidor(player2, LocalDate.of(year, month, day++)),
            getDailyPidor(player1, LocalDate.of(year, month, day++)),
            getDailyPidor(player3, LocalDate.of(year, month, day++)),
            getDailyPidor(player1, LocalDate.of(year, month, day++)),
            getDailyPidor(player3, LocalDate.of(year, month, day++)),
            getDailyPidor(player4, LocalDate.of(year, month, day)));

    // when
    Optional<Long> topPidor = PidorUtil.getTopPidorTgId(dailyPidors);

    // then
    assertTrue(topPidor.isPresent());
    assertEquals(Long.valueOf(player3), topPidor.get());
  }

  @Test
  public void verifyGetUserName() {
    String text = "who is @PidorAmongUsLoggerBot ?";
    int offset = 7;
    int length = 22;

    // when
    String result = text.substring(offset + 1, offset + length);

    // then
    assertEquals("PidorAmongUsLoggerBot", result);
  }

  private DailyPidor getDailyPidor(long tgId, LocalDate localDate) {
    DailyPidor dailyPidor = new DailyPidor();
    dailyPidor.setPlayerTgId(tgId);
    dailyPidor.setLocalDate(localDate);
    return dailyPidor;
  }
}
