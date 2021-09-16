package by.kobyzau.tg.bot.pbot.util;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class HotPotatoUtilDeadlineTest {

  private static final int YEAR = 2021;
  private static final int MONTH = 5;
  private static final int DAY = 23;

  private final HotPotatoUtil hotPotatoUtil = new HotPotatoUtil();

  @Parameterized.Parameter public LocalDateTime currentTime;

  @Parameterized.Parameter(1)
  public LocalDateTime expectedDeadline;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {

    return Arrays.asList(
        new Object[] {time(0, 0), time(9, 30)},
        new Object[] {time(10, 0), time(14, 30)},
        new Object[] {time(10, 40), time(14, 50)},
        new Object[] {time(14, 30), time(16, 45)},
        new Object[] {time(17, 40), time(18, 20)},
        new Object[] {time(18, 0), time(18, 30)},
        new Object[] {time(18, 40), time(18, 50)},
        new Object[] {time(19, 0), time(19, 0)},
        new Object[] {time(20, 15), time(20, 15)});
  }

  @Test
  public void getDeadline_test() {
    // when
    LocalDateTime result = hotPotatoUtil.getDeadline(currentTime);

    // then
    assertEquals("From " + currentTime, expectedDeadline, result);
  }

  private static LocalDateTime time(int hour, int minute) {
    return LocalDateTime.of(YEAR, MONTH, DAY, hour, minute);
  }
}
