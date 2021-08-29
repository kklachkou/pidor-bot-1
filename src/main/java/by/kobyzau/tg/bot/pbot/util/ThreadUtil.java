package by.kobyzau.tg.bot.pbot.util;

public class ThreadUtil {

  public static void sleep(long mils) {
    try {
      Thread.sleep(mils);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
