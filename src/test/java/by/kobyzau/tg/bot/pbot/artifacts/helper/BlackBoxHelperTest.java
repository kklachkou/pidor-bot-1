package by.kobyzau.tg.bot.pbot.artifacts.helper;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static by.kobyzau.tg.bot.pbot.artifacts.ArtifactType.*;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class BlackBoxHelperTest {

  @Mock private PidorService pidorService;

  @InjectMocks private BlackBoxHelper helper;

  private static final long CHAT_ID = 123;

  @Test
  public void getArtifactsForBox_noAntiPidors() {
    // given
    doReturn(
            Arrays.asList(
                Pidor.builder().artifacts(Collections.emptySet()).build(),
                Pidor.builder().artifacts(Collections.singleton(PIDOR_MAGNET)).build()))
        .when(pidorService)
        .getByChat(CHAT_ID);

    // when
    List<ArtifactType> artifactsForBox = helper.getArtifactsForBox(CHAT_ID);

    // then
    Assert.assertEquals(
        Arrays.asList(
            PIDOR_MAGNET,
            SECOND_CHANCE,
            SILENCE,
            BLINDNESS,
            RICOCHET,
            ANTI_PIDOR,
            HELL_FIRE,
            SUPER_VOTE,
            PIDOR_MAGNET),
        artifactsForBox);
  }

  @Test
  public void getArtifactsForBox_hasAntiPidors() {
    // given
    doReturn(
            Arrays.asList(
                Pidor.builder().artifacts(Collections.emptySet()).build(),
                Pidor.builder().artifacts(Collections.singleton(ANTI_PIDOR)).build(),
                Pidor.builder().artifacts(Collections.singleton(PIDOR_MAGNET)).build()))
        .when(pidorService)
        .getByChat(CHAT_ID);

    // when
    List<ArtifactType> artifactsForBox = helper.getArtifactsForBox(CHAT_ID);

    // then
    Assert.assertEquals(
        Arrays.asList(
            PIDOR_MAGNET,
            SECOND_CHANCE,
            SILENCE,
            BLINDNESS,
            RICOCHET,
            HELL_FIRE,
            SUPER_VOTE,
            PIDOR_MAGNET),
        artifactsForBox);
  }
}
