package by.kobyzau.tg.bot.pbot.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilTest {

  private static final int DEFAULT = -1;
  private static final String DEFAULT_STR = "default";

  @Test
  public void parseInt_null() {
    // when
    int result = StringUtil.parseInt(null, DEFAULT);

    // then
    assertEquals(DEFAULT, result);
  }

  @Test
  public void escapeAllSpecialChars_withSpecialChars() {
    //given
    String original = "abc .123\uD83D\uDE4A@#+";

    //when
    String result = StringUtil.escapeAllSpecialChars(original);

    //then
    assertEquals("abc .123.....", result);
  }

  @Test
  public void parseInt_empty() {
    // given
    String s = "";
    // when
    int result = StringUtil.parseInt(s, DEFAULT);

    // then
    assertEquals(DEFAULT, result);
  }

  @Test
  public void parseInt_letter() {
    // given
    String s = "a";
    // when
    int result = StringUtil.parseInt(s, DEFAULT);

    // then
    assertEquals(DEFAULT, result);
  }

  @Test
  public void parseInt_mixed() {
    // given
    String s = "123abc";
    // when
    int result = StringUtil.parseInt(s, DEFAULT);

    // then
    assertEquals(DEFAULT, result);
  }

  @Test
  public void parseInt_double() {
    // given
    String s = "123.10";
    // when
    int result = StringUtil.parseInt(s, DEFAULT);

    // then
    assertEquals(DEFAULT, result);
  }

  @Test
  public void parseInt_int() {
    // given
    String s = "123";
    // when
    int result = StringUtil.parseInt(s, DEFAULT);

    // then
    assertEquals(123, result);
  }

  @Test
  public void parseLong_null() {
    // when
    long result = StringUtil.parseLong(null, DEFAULT);

    // then
    assertEquals(DEFAULT, result);
  }

  @Test
  public void parseLong_empty() {
    // given
    String s = "";

    // when
    long result = StringUtil.parseLong(s, DEFAULT);

    // then
    assertEquals(DEFAULT, result);
  }

  @Test
  public void parseLong_letter() {
    // given
    String s = "a";

    // when
    long result = StringUtil.parseLong(s, DEFAULT);

    // then
    assertEquals(DEFAULT, result);
  }

  @Test
  public void parseLong_mixed() {
    // given
    String s = "123abc";

    // when
    long result = StringUtil.parseLong(s, DEFAULT);

    // then
    assertEquals(DEFAULT, result);
  }

  @Test
  public void parseLong_double() {
    // given
    String s = "123.10";

    // when
    long result = StringUtil.parseLong(s, DEFAULT);

    // then
    assertEquals(DEFAULT, result);
  }

  @Test
  public void parseLong_long() {
    // given
    String s = "123";
    // when
    long result = StringUtil.parseLong(s, DEFAULT);

    // then
    assertEquals(123, result);
  }

  @Test
  public void repeat_zeroTimes() {
    // given
    int times = 0;
    String text = "*";

    // when
    String result = StringUtil.repeat(text, times);

    // then
    assertEquals("", result);
  }

  @Test
  public void repeat_oneTime() {
    // given
    int times = 1;
    String text = "*";

    // when
    String result = StringUtil.repeat(text, times);

    // then
    assertEquals(text, result);
  }

  @Test
  public void repeat_manyTimes() {
    // given
    int times = 4;
    String text = "*";

    // when
    String result = StringUtil.repeat(text, times);

    // then
    assertEquals("****", result);
  }

  @Test
  public void trim_null() {
    // when
    String result = StringUtil.trim(null);

    // then
    assertEquals("", result);
  }

  @Test
  public void trim_empty() {
    // given
    String text = "";

    // when
    String result = StringUtil.trim(text);

    // then
    assertEquals("", result);
  }

  @Test
  public void trim_space() {
    // given
    String text = "  ";

    // when
    String result = StringUtil.trim(text);

    // then
    assertEquals("", result);
  }

  @Test
  public void trim_textWithoutSpaces() {
    // given
    String text = "abc";

    // when
    String result = StringUtil.trim(text);

    // then
    assertEquals(text, result);
  }

  @Test
  public void trim_textWithSpaces() {
    // given
    String text = "  abc ";

    // when
    String result = StringUtil.trim(text);

    // then
    assertEquals("abc", result);
  }

  @Test
  public void isBlank_withDefault_null() {
    // when
    String result = StringUtil.isBlank(null, DEFAULT_STR);

    // then
    assertEquals(DEFAULT_STR, result);
  }

  @Test
  public void isBlank_withDefault_empty() {
    // given
    String text = "";

    // when
    String result = StringUtil.isBlank(text, DEFAULT_STR);

    // then
    assertEquals(DEFAULT_STR, result);
  }

  @Test
  public void isBlank_withDefault_space() {
    // given
    String text = "  ";

    // when
    String result = StringUtil.isBlank(text, DEFAULT_STR);

    // then
    assertEquals(DEFAULT_STR, result);
  }

  @Test
  public void isBlank_withDefault_text() {
    // given
    String text = "abc ";

    // when
    String result = StringUtil.isBlank(text, DEFAULT_STR);

    // then
    assertEquals(text, result);
  }

  @Test
  public void isBlank_null() {
    // when
    boolean result = StringUtil.isBlank(null);

    // then
    assertTrue(result);
  }

  @Test
  public void isBlank_empty() {
    // given
    String text = "";

    // when
    boolean result = StringUtil.isBlank(text);

    // then
    assertTrue(result);
  }

  @Test
  public void isBlank_space() {
    // given
    String text = "  ";

    // when
    boolean result = StringUtil.isBlank(text);

    // then
    assertTrue(result);
  }

  @Test
  public void isBlank_text() {
    // given
    String text = "abc";

    // when
    boolean result = StringUtil.isBlank(text);

    // then
    assertFalse(result);
  }

  @Test
  public void isNotBlank_null() {
    // when
    boolean result = StringUtil.isNotBlank(null);

    // then
    assertFalse(result);
  }

  @Test
  public void isNotBlank_empty() {
    // when
    String text = "";

    // when
    boolean result = StringUtil.isNotBlank(text);

    // then
    assertFalse(result);
  }

  @Test
  public void isNotBlank_space() {
    // when
    String text = "  ";

    // when
    boolean result = StringUtil.isNotBlank(text);

    // then
    assertFalse(result);
  }

  @Test
  public void isNotBlank_text() {
    // when
    String text = "abc";

    // when
    boolean result = StringUtil.isNotBlank(text);

    // then
    assertTrue(result);
  }

  @Test
  public void equals_bothNulls() {
    // given
    String s1 = null;
    String s2 = null;

    // when
    boolean result = StringUtil.equals(s1, s2);

    // then
    assertTrue(result);
  }

  @Test
  public void equals_firstNulls() {
    // given
    String s1 = null;
    String s2 = "s2";

    // when
    boolean result = StringUtil.equals(s1, s2);

    // then
    assertFalse(result);
  }

  @Test
  public void equals_secondNulls() {
    // given
    String s1 = "s1";
    String s2 = null;

    // when
    boolean result = StringUtil.equals(s1, s2);

    // then
    assertFalse(result);
  }

  @Test
  public void equals_different() {
    // given
    String s1 = "s1";
    String s2 = "s2";

    // when
    boolean result = StringUtil.equals(s1, s2);

    // then
    assertFalse(result);
  }

  @Test
  public void equals_same() {
    // given
    String s1 = "text";
    String s2 = "text";

    // when
    boolean result = StringUtil.equals(s1, s2);

    // then
    assertTrue(result);
  }

  @Test
  public void substringBefore_null() {
    // given
    String text = null;
    String separator = "s";

    // when
    String result = StringUtil.substringBefore(text, separator);

    // then
    assertNull(result);
  }

  @Test
  public void substringBefore_empty() {
    // given
    String text = "";
    String separator = "s";

    // when
    String result = StringUtil.substringBefore(text, separator);

    // then
    assertEquals(text, result);
  }

  @Test
  public void substringBefore_space() {
    // given
    String text = " ";
    String separator = "s";

    // when
    String result = StringUtil.substringBefore(text, separator);

    // then
    assertEquals(text, result);
  }

  @Test
  public void substringBefore_noSeparators() {
    // given
    String text = "abc";
    String separator = "s";

    // when
    String result = StringUtil.substringBefore(text, separator);

    // then
    assertEquals("", result);
  }

  @Test
  public void substringBefore_separatorInStart() {
    // given
    String text = "sabc";
    String separator = "s";

    // when
    String result = StringUtil.substringBefore(text, separator);

    // then
    assertEquals("", result);
  }

  @Test
  public void substringBefore_separatorInMiddle() {
    // given
    String text = "absc";
    String separator = "s";

    // when
    String result = StringUtil.substringBefore(text, separator);

    // then
    assertEquals("ab", result);
  }

  @Test
  public void substringBefore_separatorInEnd() {
    // given
    String text = "abcs";
    String separator = "s";

    // when
    String result = StringUtil.substringBefore(text, separator);

    // then
    assertEquals("abc", result);
  }

  @Test
  public void substringBefore_separatorInMiddleMultiple() {
    // given
    String text = "asbsc";
    String separator = "s";

    // when
    String result = StringUtil.substringBefore(text, separator);

    // then
    assertEquals("a", result);
  }
}
