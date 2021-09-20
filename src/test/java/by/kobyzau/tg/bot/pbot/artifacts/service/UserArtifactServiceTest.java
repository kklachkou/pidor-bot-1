package by.kobyzau.tg.bot.pbot.artifacts.service;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import by.kobyzau.tg.bot.pbot.artifacts.repository.UserArtifactRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserArtifactServiceTest {

  @Mock private UserArtifactRepository userArtifactRepository;

  private UserArtifactService service;

  private static final long CHAT_ID = 123;

  @Before
  public void init() {
    service = new UserArtifactServiceImpl(userArtifactRepository);
  }

  @Test
  public void addArtifact_test() {
    // given
    long userId = 1;
    ArtifactType artifactType = ArtifactType.PIDOR_MAGNET;
    LocalDate date = LocalDate.now();

    // when
    service.addArtifact(CHAT_ID, userId, artifactType, date);

    // then
    verify(userArtifactRepository)
        .create(
            UserArtifact.builder()
                .chatId(CHAT_ID)
                .userId(userId)
                .artifactType(artifactType)
                .date(date)
                .build());
  }

  @Test
  public void deleteArtifact_test() {
    // given
    long artefactId = 10;

    // when
    service.deleteArtifact(artefactId);

    // then
    verify(userArtifactRepository).delete(artefactId);
  }

  @Test
  public void getUserArtifacts_chat() {
    // given
    LocalDate date = LocalDate.now();
    doReturn(
            Arrays.asList(
                UserArtifact.builder().id(1).date(date).build(),
                UserArtifact.builder().id(2).date(date.plusDays(1)).build(),
                UserArtifact.builder().id(3).date(date).build()))
        .when(userArtifactRepository)
        .getByChatId(CHAT_ID);

    // when
    List<UserArtifact> result = service.getUserArtifacts(CHAT_ID);

    // then
    assertEquals(
        Arrays.asList(
            UserArtifact.builder().id(1).date(date).build(),
            UserArtifact.builder().id(2).date(date.plusDays(1)).build(),
            UserArtifact.builder().id(3).date(date).build()),
        result);
  }

  @Test
  public void getUserArtifacts_chatAndUser() {
    // given
    long userId = 1;
    doReturn(
            Arrays.asList(
                UserArtifact.builder().id(1).userId(userId).build(),
                UserArtifact.builder().id(2).userId(userId + 1).build(),
                UserArtifact.builder().id(3).userId(userId).build()))
        .when(userArtifactRepository)
        .getByChatId(CHAT_ID);

    // when
    List<UserArtifact> result = service.getUserArtifacts(CHAT_ID, userId);

    // then
    assertEquals(
        Arrays.asList(
            UserArtifact.builder().id(1).userId(userId).build(),
            UserArtifact.builder().id(3).userId(userId).build()),
        result);
  }

  @Test
  public void getUserArtifact_test() {
    // given
    long userId = 1;
    ArtifactType artifactType = ArtifactType.PIDOR_MAGNET;
    LocalDate date = LocalDate.now();
    doReturn(
            Arrays.asList(
                UserArtifact.builder()
                    .id(1)
                    .userId(userId + 1)
                    .artifactType(artifactType)
                    .date(date)
                    .build(),
                UserArtifact.builder()
                    .id(2)
                    .userId(userId)
                    .artifactType(artifactType)
                    .date(date)
                    .build(),
                UserArtifact.builder()
                    .id(3)
                    .userId(userId)
                    .artifactType(ArtifactType.SECOND_CHANCE)
                    .date(date)
                    .build()))
        .when(userArtifactRepository)
        .getByChatId(CHAT_ID);

    // when
    Optional<UserArtifact> result = service.getUserArtifact(CHAT_ID, userId, artifactType);

    // then
    assertTrue(result.isPresent());
    assertEquals(
        UserArtifact.builder().id(2).userId(userId).artifactType(artifactType).date(date).build(),
        result.get());
  }

  @Test
  public void clearUserArtifacts_test() {
    // given
    LocalDate date = LocalDate.now();
    doReturn(
            Arrays.asList(
                UserArtifact.builder()
                    .artifactType(ArtifactType.PIDOR_MAGNET)
                    .id(1)
                    .build(),
                UserArtifact.builder()
                    .artifactType(ArtifactType.PIDOR_MAGNET)
                    .id(2)
                    .build(),
                UserArtifact.builder()
                    .artifactType(ArtifactType.SECOND_CHANCE)
                    .id(3)
                    .build()))
        .when(userArtifactRepository)
        .getByChatId(CHAT_ID);

    // when
    service.clearUserArtifacts(CHAT_ID, ArtifactType.PIDOR_MAGNET);

    // then
    verify(userArtifactRepository).delete(1);
    verify(userArtifactRepository).delete(2);
    verify(userArtifactRepository, times(0)).delete(3);
  }
}
