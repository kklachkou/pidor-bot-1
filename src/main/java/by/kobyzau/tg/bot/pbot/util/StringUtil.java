package by.kobyzau.tg.bot.pbot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class StringUtil {

  private static final Pattern SIMPLE_CHAR = Pattern.compile("[a-zA-Zа-яА-Я0-9 ]");

  public static String escapeAllSpecialChars(String text) {
    if (isBlank(text)) {
      return "";
    }
    List<char[]> chars = Arrays.asList(text.toCharArray());
    StringBuilder sb = new StringBuilder();
    for (char c : text.toCharArray()) {
      if (SIMPLE_CHAR.matcher(String.valueOf(c)).matches()) {
        sb.append(c);
      } else {
        sb.append(".");
      }
    }
    return sb.toString();
  }

  public static int parseInt(String s, int def) {
    try {
      return Integer.parseInt(trim(s));
    } catch (Exception e) {
      return def;
    }
  }

  public static long parseLong(String s, long def) {
    try {
      return Long.parseLong(trim(s));
    } catch (Exception e) {
      return def;
    }
  }

  public static String repeat(String s, int times) {
    if (times <= 0) {
      return "";
    }
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < times; i++) {
      sb.append(s);
    }
    return sb.toString();
  }

  public static String trim(String s) {
    if (isBlank(s)) {
      return "";
    }
    return s.trim();
  }

  public static String isBlank(String s, String def) {
    return isNotBlank(s) ? s : def;
  }

  public static boolean isNotBlank(String s) {
    return !isBlank(s);
  }

  public static boolean isBlank(String s) {
    if (s == null) {
      return true;
    }
    return s.trim().length() == 0;
  }

  public static boolean equals(String s1, String s2) {
    if (s1 == null && s2 == null) {
      return true;
    }
    if (s1 == null || s2 == null) {
      return false;
    }
    return s1.trim().equals(s2.trim());
  }

  public static String substringBefore(final String str, final String separator) {
    if (isBlank(str) || separator == null) {
      return str;
    }
    if (separator.isEmpty()) {
      return str;
    }
    final int pos = str.indexOf(separator);
    if (pos == -1) {
      return "";
    }
    return str.substring(0, pos);
  }

  public static String serialize(Object o) {
    try {
      return new ObjectMapper().writeValueAsString(o);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Cannot serialize " + o, e);
    }
  }

  public static <T> Optional<T> deserialize(String json, Class<T> c) {
    try {
      return Optional.ofNullable(new ObjectMapper().readValue(json, c));
    } catch (Exception e) {
      return Optional.empty();
    }
  }
}
