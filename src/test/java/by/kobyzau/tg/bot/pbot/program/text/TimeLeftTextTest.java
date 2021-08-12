package by.kobyzau.tg.bot.pbot.program.text;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TimeLeftTextTest {

  private static final int YEAR = 2021;
  private static final int MONTH = 5;
  private static final int DAY = 23;

  @Parameterized.Parameter public LocalDateTime startTime;

  @Parameterized.Parameter(1)
  public LocalDateTime endTime;

  @Parameterized.Parameter(2)
  public String expected;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {

    return Arrays.asList(
        new Object[] {time(2, 30), time(3, 40), "1ч 10мин"},
        new Object[] {time(2, 30), time(3, 10), "0ч 40мин"},
        new Object[] {time(1, 0), time(3, 0), "2ч 0мин"},
        new Object[] {time(1, 0), time(1, 10), "0ч 10мин"},
        new Object[] {time(1, 20), time(1, 10), "0ч 0мин"});
  }

  @Test
  public void text_test() {
    // given
    Text text = new TimeLeftText(startTime, endTime);

    // when
    String result = text.text();

    // then
    Assert.assertEquals("From " + startTime + " till " + endTime, expected, result);
  }

  private static LocalDateTime time(int hour, int minute) {
    return LocalDateTime.of(YEAR, MONTH, DAY, hour, minute);
  }
}
