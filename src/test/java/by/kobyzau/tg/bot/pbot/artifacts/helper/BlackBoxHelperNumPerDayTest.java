package by.kobyzau.tg.bot.pbot.artifacts.helper;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(Parameterized.class)
public class BlackBoxHelperNumPerDayTest {
  private PidorService pidorService;
  private BlackBoxHelper blackBoxHelper;

  @Parameterized.Parameter public int numPidors;

  @Parameterized.Parameter(1)
  public int expected;

  @Parameterized.Parameters
  public static List<Object[]> getParams() {
    return Arrays.asList(
        new Object[] {0, 1},
        new Object[] {1, 1},
        new Object[] {2, 1},
        new Object[] {3, 1},
        new Object[] {4, 2},
        new Object[] {5, 2},
        new Object[] {6, 3},
        new Object[] {7, 3},
        new Object[] {8, 4},
        new Object[] {9, 4});
  }

  private static final long CHAT_ID = 123;

  @Before
  public void init() {
    pidorService = Mockito.mock(PidorService.class);
    blackBoxHelper = new BlackBoxHelper(pidorService);
  }

  @Test
  public void getNumArtifactsPerDay_test() {
    // given
    List<Pidor> pidors = new ArrayList<>();
    for (int i = 0; i < numPidors; i++) {
      pidors.add(new Pidor());
    }
    doReturn(pidors).when(pidorService).getByChat(CHAT_ID);

    // when
    int numArtifactsPerDay = blackBoxHelper.getNumArtifactsPerDay(CHAT_ID);

    // then
    assertEquals(expected, numArtifactsPerDay);
  }
}
