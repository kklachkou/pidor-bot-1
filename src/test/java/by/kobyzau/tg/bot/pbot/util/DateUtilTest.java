package by.kobyzau.tg.bot.pbot.util;

import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.Assert.*;

public class DateUtilTest {

  @Test
  public void isToday_null() {
    // when
    boolean today = DateUtil.isToday(null);

    // then
    assertFalse(today);
  }

  @Test
  public void isToday_notToday() {
    // given
    LocalDate date = DateUtil.now().minusDays(1);

    // when
    boolean today = DateUtil.isToday(date);

    // then
    assertFalse(today);
  }

  @Test
  public void isToday_today() {
    // given
    LocalDate date = DateUtil.now();

    // when
    boolean today = DateUtil.isToday(date);

    // then
    assertTrue(today);
  }

  @Test
  public void equals_bothNulls() {
    // when
    boolean result = DateUtil.equals(null, null);

    // then
    assertTrue(result);
  }

  @Test
  public void equals_firstNull() {
    // given
    LocalDate d2 = DateUtil.now();

    // when
    boolean result = DateUtil.equals(null, d2);

    // then
    assertFalse(result);
  }

  @Test
  public void equals_secondNull() {
    // given
    LocalDate d1 = DateUtil.now();

    // when
    boolean result = DateUtil.equals(d1, null);

    // then
    assertFalse(result);
  }

  @Test
  public void equals_different() {
    // given
    LocalDate d1 = DateUtil.now();
    LocalDate d2 = DateUtil.now().plusDays(1);

    // when
    boolean result = DateUtil.equals(d1, d2);

    // then
    assertFalse(result);
  }

  @Test
  public void equals_same() {
    // given
    LocalDate d1 = DateUtil.now();
    LocalDate d2 = DateUtil.now();

    // when
    boolean result = DateUtil.equals(d1, d2);

    // then
    assertTrue(result);
  }

  @Test
  public void minskZone_minsk() {
    //when
    ZoneId zoneId = DateUtil.minskZone();

    //then
    assertEquals("UTC+03:00", zoneId.getId());
  }
}
