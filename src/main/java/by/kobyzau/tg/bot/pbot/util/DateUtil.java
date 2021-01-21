package by.kobyzau.tg.bot.pbot.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtil {

  public static boolean isToday(LocalDate localDate) {
    if (localDate == null) {
      return false;
    }
    LocalDate now = now();
    return now.isEqual(localDate);
  }

  public static boolean equals(LocalDate d1, LocalDate d2) {
    if (d1 == null && d2 == null) {
      return true;
    }
    if (d1 == null || d2 == null) {
      return true;
    }
    return d1.isEqual(d2);
  }

  public static LocalDate now() {
    return LocalDate.now(minskZone());
  }

  public static LocalDateTime currentTime() {
    return LocalDateTime.now(minskZone());
  }

  public static boolean sleepTime(){
    int hour = currentTime().getHour();
    return hour < 7 || hour == 23;
  }

  public static boolean isNewYearTime() {
    LocalDate now = now();
    return now.getDayOfYear() < 15 || now.getDayOfYear() > 330;
  }
  public static ZoneId minskZone() {
    return ZoneId.of("UTC+3");
  }
}
