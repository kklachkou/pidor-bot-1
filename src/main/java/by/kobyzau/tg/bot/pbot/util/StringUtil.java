package by.kobyzau.tg.bot.pbot.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Optional;

public class StringUtil {

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
    int len = s.length();
    int start = 0;
    for (; start < len; start++) {
      char c = s.charAt(start);
      if (c < '!') {
        continue;
      }
      if (c <= '~') {
        return false;
      }
      if (Character.isWhitespace(c) || Character.isSpaceChar(c)) {
        continue;
      }
      return false;
    }
    return true;
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

  public static boolean equalsIgnoreCase(String s1, String s2) {
    if (s1 == null && s2 == null) {
      return true;
    }
    if (s1 == null || s2 == null) {
      return false;
    }
    return s1.trim().equalsIgnoreCase(s2.trim());
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
    } catch (IOException e) {
      return Optional.empty();
    }
  }
}
