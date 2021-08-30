package by.kobyzau.tg.bot.pbot.util;

import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

public class CollectionUtilTest {

  @Test
  public void getItem_zero() {
    // given
    List<String> list = Arrays.asList("1", "2", "3", "4");

    // when
    String value = CollectionUtil.getItem(list, 0);

    // then
    assertEquals("1", value);
  }

  @Test
  public void isNotEmpty_null() {
    // when
    boolean result = CollectionUtil.isNotEmpty(null);

    // then
    assertFalse(result);
  }

  @Test
  public void isNotEmpty_empty() {
    // when
    Collection<Object> c = Collections.emptyList();
    // when
    boolean result = CollectionUtil.isNotEmpty(c);

    // then
    assertFalse(result);
  }

  @Test
  public void isNotEmpty_notEmpty() {
    // when
    Collection<Object> c = Arrays.asList("a", "b");
    // when
    boolean result = CollectionUtil.isNotEmpty(c);

    // then
    assertTrue(result);
  }

  @Test
  public void size_null_zero() {
    // when
    int size = CollectionUtil.size(null);

    // then
    assertEquals(0, size);
  }

  @Test
  public void size_empty_zero() {
    // given
    List<Object> list = Collections.emptyList();

    // when
    int size = CollectionUtil.size(list);

    // then
    assertEquals(0, size);
  }

  @Test
  public void size_notEmpty() {
    // given
    List<Object> list = Arrays.asList("one", "two");

    // when
    int size = CollectionUtil.size(list);

    // then
    assertEquals(2, size);
  }

  @Test
  public void getItem_lessSize() {
    // given
    List<String> list = Arrays.asList("1", "2", "3", "4");

    // when
    String value = CollectionUtil.getItem(list, 2);

    // then
    assertEquals("3", value);
  }

  @Test
  public void getItem_eqSize() {
    // given
    List<String> list = Arrays.asList("1", "2", "3", "4");

    // when
    String value = CollectionUtil.getItem(list, 3);

    // then
    assertEquals("4", value);
  }

  @Test
  public void getItem_moreSize() {
    // given
    List<String> list = Arrays.asList("1", "2", "3", "4");

    // when
    String value = CollectionUtil.getItem(list, 4);

    // then
    assertEquals("1", value);
  }

  @Test
  public void getItem_MatchMoreSize() {
    // given
    List<String> list = Arrays.asList("1", "2", "3", "4");

    // when
    String value = CollectionUtil.getItem(list, 9);

    // then
    assertEquals("2", value);
  }

  @Test
  public void getRandomListByDay_test() {
    List<LocalDate> dates =
        Arrays.asList(
            LocalDate.of(2021, 10, 1),
            LocalDate.of(2021, 10, 2),
            LocalDate.of(2021, 10, 3),
            LocalDate.of(2021, 10, 4),
            LocalDate.of(2021, 10, 5),
            LocalDate.of(2021, 10, 6),
            LocalDate.of(2021, 11, 11),
            LocalDate.of(2021, 11, 12));
    List<String> list = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    Set<List<String>> allResults = new HashSet<>();
    for (LocalDate date : dates) {
      List<String> randomListByDay = CollectionUtil.getRandomListByDay(list, date);
      allResults.add(randomListByDay);
      System.out.println("Date " + date + ": " + randomListByDay);
      for (int iteration = 0; iteration < 10; iteration++) {
        assertEquals(
            "Value is changed for same list and date",
            randomListByDay,
            CollectionUtil.getRandomListByDay(list, date));
      }
    }
    assertNotEquals("Same results for different days", 1, allResults.size());
  }
}
