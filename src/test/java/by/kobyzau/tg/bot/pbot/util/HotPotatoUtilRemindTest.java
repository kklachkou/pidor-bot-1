package by.kobyzau.tg.bot.pbot.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class HotPotatoUtilRemindTest {

  private static final int YEAR = 2021;
  private static final int MONTH = 5;
  private static final int DAY = 23;

  private final HotPotatoUtil hotPotatoUtil = new HotPotatoUtil();

  @Parameterized.Parameter public LocalDateTime deadline;

  @Parameterized.Parameter(1)
  public LocalDateTime currentTime;

  @Parameterized.Parameter(2)
  public boolean shouldRemind;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {

    return Arrays.asList(
        new Object[] {time(10, 20, 0), time(9, 15, 59), false},
        new Object[] {time(10, 20, 0), time(9, 15, 31), false},
        new Object[] {time(10, 20, 0), time(9, 15, 29), false},
        new Object[] {time(10, 20, 0), time(9, 15, 0), false},
        new Object[] {time(10, 20, 0), time(11, 15, 59), false},
        new Object[] {time(10, 20, 0), time(11, 15, 31), false},
        new Object[] {time(10, 20, 0), time(11, 15, 29), false},
        new Object[] {time(10, 20, 0), time(11, 15, 0), false},
        new Object[] {time(10, 20, 0), time(10, 14, 59), false},
        new Object[] {time(10, 20, 0), time(10, 14, 31), false},
        new Object[] {time(10, 20, 0), time(10, 14, 29), false},
        new Object[] {time(10, 20, 0), time(10, 14, 0), false},
        new Object[] {time(10, 20, 0), time(10, 16, 59), false},
        new Object[] {time(10, 20, 0), time(10, 16, 31), false},
        new Object[] {time(10, 20, 0), time(10, 16, 29), false},
        new Object[] {time(10, 20, 0), time(10, 16, 0), false},
        new Object[] {time(10, 20, 0), time(10, 15, 59), true},
        new Object[] {time(10, 20, 0), time(10, 15, 31), true},
        new Object[] {time(10, 20, 0), time(10, 15, 29), false},
        new Object[] {time(10, 20, 0), time(10, 15, 0), false},
        new Object[] {time(10, 20, 50), time(10, 15, 59), true},
        new Object[] {time(10, 20, 50), time(10, 15, 31), true},
        new Object[] {time(10, 20, 50), time(10, 15, 29), false},
        new Object[] {time(10, 20, 50), time(10, 15, 0), false},
        new Object[] {time(17, 20, 0), time(17, 15, 59), true},
        new Object[] {time(17, 20, 0), time(17, 15, 31), true},
        new Object[] {time(17, 20, 0), time(17, 15, 29), false},
        new Object[] {time(17, 20, 0), time(17, 15, 0), false},
        new Object[] {time(17, 20, 50), time(17, 15, 59), true},
        new Object[] {time(17, 20, 50), time(17, 15, 31), true},
        new Object[] {time(17, 20, 50), time(17, 15, 29), false},
        new Object[] {time(17, 20, 50), time(17, 15, 0), false},
        new Object[] {time(18, 20, 0), time(18, 15, 59), false},
        new Object[] {time(18, 20, 0), time(18, 15, 31), false},
        new Object[] {time(18, 20, 0), time(18, 15, 29), false},
        new Object[] {time(18, 20, 0), time(18, 15, 0), false},
        new Object[] {time(18, 20, 50), time(18, 15, 59), false},
        new Object[] {time(18, 20, 50), time(18, 15, 31), false},
        new Object[] {time(18, 20, 50), time(18, 15, 29), false},
        new Object[] {time(18, 20, 50), time(18, 15, 0), false});
  }

  @Test
  public void getDeadline_test() {
    // when
    boolean shouldRemind = hotPotatoUtil.shouldRemind(deadline, currentTime);

    // then
    Assert.assertEquals(
        "From " + currentTime + " with deadline " + deadline, this.shouldRemind, shouldRemind);
  }

  private static LocalDateTime time(int hour, int minute, int seconds) {
    return LocalDateTime.of(YEAR, MONTH, DAY, hour, minute, seconds);
  }
}
