package by.kobyzau.tg.bot.pbot.util;

public class IntUtil {

  public static boolean inRange(Integer value, int from, int to) {
    if (value == null){
      return false;
    }
    return value >= from && value <= to;
  }
}
