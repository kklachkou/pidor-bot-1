package by.kobyzau.tg.bot.pbot.program.logger;

import static by.kobyzau.tg.bot.pbot.program.logger.LoggerLevel.DEBUG;
import static by.kobyzau.tg.bot.pbot.program.logger.LoggerLevel.ERROR;
import static by.kobyzau.tg.bot.pbot.program.logger.LoggerLevel.INFO;
import static by.kobyzau.tg.bot.pbot.program.logger.LoggerLevel.WARN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class LoggerLevelTest {

  @Parameterized.Parameter public LoggerLevel currentLevel;

  @Parameterized.Parameter(1)
  public LoggerLevel inputLevel;

  @Parameterized.Parameter(2)
  public boolean expectedResult;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {

    List<Object[]> params = new ArrayList<>();
    params.add(new Object[] {ERROR, null, false});
    params.add(new Object[] {ERROR, ERROR, true});
    params.add(new Object[] {ERROR, WARN, false});
    params.add(new Object[] {ERROR, INFO, false});
    params.add(new Object[] {ERROR, DEBUG, false});

    params.add(new Object[] {WARN, null, false});
    params.add(new Object[] {WARN, ERROR, true});
    params.add(new Object[] {WARN, WARN, true});
    params.add(new Object[] {WARN, INFO, false});
    params.add(new Object[] {WARN, DEBUG, false});

    params.add(new Object[] {INFO, null, false});
    params.add(new Object[] {INFO, ERROR, true});
    params.add(new Object[] {INFO, WARN, true});
    params.add(new Object[] {INFO, INFO, true});
    params.add(new Object[] {INFO, DEBUG, false});

    params.add(new Object[] {DEBUG, null, false});
    params.add(new Object[] {DEBUG, ERROR, true});
    params.add(new Object[] {DEBUG, WARN, true});
    params.add(new Object[] {DEBUG, INFO, true});
    params.add(new Object[] {DEBUG, DEBUG, true});
    return params;
  }

  @Test
  public void matchLevel() {

    // when
    boolean result = currentLevel.matchLevel(inputLevel);

    // then
    Assert.assertEquals(expectedResult, result);
  }
}
