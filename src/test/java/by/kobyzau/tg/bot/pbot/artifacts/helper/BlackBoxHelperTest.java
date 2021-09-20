package by.kobyzau.tg.bot.pbot.artifacts.helper;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class BlackBoxHelperTest {

  @Mock private PidorService pidorService;

  @InjectMocks private BlackBoxHelper blackBoxHelper;

  private static final long CHAT_ID = 123;

  @Test
  public void getNumArtifactsPerDay_noPidors() {
    // given
    doReturn(Collections.emptyList()).when(pidorService).getByChat(CHAT_ID);

    // when
    int numArtifactsPerDay = blackBoxHelper.getNumArtifactsPerDay(CHAT_ID);

    // then
    assertEquals(1, numArtifactsPerDay);
  }

  @Test
  public void getNumArtifactsPerDay_onePidor() {
    // given
    doReturn(Collections.singletonList(new Pidor())).when(pidorService).getByChat(CHAT_ID);

    // when
    int numArtifactsPerDay = blackBoxHelper.getNumArtifactsPerDay(CHAT_ID);

    // then
    assertEquals(1, numArtifactsPerDay);
  }

  @Test
  public void getNumArtifactsPerDay_twoPidors() {
    // given
    doReturn(Arrays.asList(new Pidor(), new Pidor())).when(pidorService).getByChat(CHAT_ID);

    // when
    int numArtifactsPerDay = blackBoxHelper.getNumArtifactsPerDay(CHAT_ID);

    // then
    assertEquals(1, numArtifactsPerDay);
  }

  @Test
  public void getNumArtifactsPerDay_fewPidors() {
    // given
    doReturn(
            Arrays.asList(
                new Pidor(), new Pidor(), new Pidor(), new Pidor(), new Pidor(), new Pidor()))
        .when(pidorService)
        .getByChat(CHAT_ID);

    // when
    int numArtifactsPerDay = blackBoxHelper.getNumArtifactsPerDay(CHAT_ID);

    // then
    assertEquals(3, numArtifactsPerDay);
  }
}
