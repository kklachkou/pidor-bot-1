package by.kobyzau.tg.bot.pbot.service.github;

import by.kobyzau.tg.bot.pbot.client.GithubClient;
import by.kobyzau.tg.bot.pbot.model.api.github.CommitInfoDto;
import by.kobyzau.tg.bot.pbot.model.api.github.CommitResponseDto;
import by.kobyzau.tg.bot.pbot.model.dto.AppVersionDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class GithubServiceTest {

  private static final String USER = "user";
  private static final String REPO = "repo";
  private static final String BRANCH = "branch";

  @Mock private GithubClient githubClient;

  @InjectMocks
  private GithubService service = new GithubServiceImpl();

  @Before
  public void init() {
    ReflectionTestUtils.setField(service, "user", USER);
    ReflectionTestUtils.setField(service, "repositoryName", REPO);
    ReflectionTestUtils.setField(service, "branchName", BRANCH);
  }

  @Test
  public void getAppVersion_noResponse() {
    // given
    doReturn(Optional.empty()).when(githubClient).getCommit(USER, REPO, BRANCH);

    // when
    AppVersionDto appVersion = service.getAppVersion();

    // then
    assertNotNull(appVersion);
    assertEquals("-", appVersion.getNumber());
    assertEquals("-", appVersion.getName());
    assertEquals("", appVersion.getDesc());
  }

  @Test
  public void getAppVersion_noCommit() {
    // given
    doReturn(Optional.of(new CommitResponseDto())).when(githubClient).getCommit(USER, REPO, BRANCH);

    // when
    AppVersionDto appVersion = service.getAppVersion();

    // then
    assertNotNull(appVersion);
    assertEquals("-", appVersion.getNumber());
    assertEquals("-", appVersion.getName());
    assertEquals("", appVersion.getDesc());
  }

  @Test
  public void getAppVersion_noCommitMessage() {
    // given
    doReturn(Optional.of(new CommitResponseDto(new CommitInfoDto())))
        .when(githubClient)
        .getCommit(USER, REPO, BRANCH);

    // when
    AppVersionDto appVersion = service.getAppVersion();

    // then
    assertNotNull(appVersion);
    assertEquals("-", appVersion.getNumber());
    assertEquals("-", appVersion.getName());
    assertEquals("", appVersion.getDesc());
  }

  @Test
  public void getAppVersion_emptyMessage() {
    // given
    String message = "";
    doReturn(Optional.of(new CommitResponseDto(new CommitInfoDto(message))))
        .when(githubClient)
        .getCommit(USER, REPO, BRANCH);

    // when
    AppVersionDto appVersion = service.getAppVersion();

    // then
    assertNotNull(appVersion);
    assertEquals("-", appVersion.getNumber());
    assertEquals("-", appVersion.getName());
    assertEquals("", appVersion.getDesc());
  }

  @Test
  public void getAppVersion_textMessage() {
    // given
    String message = "New Version";
    doReturn(Optional.of(new CommitResponseDto(new CommitInfoDto(message))))
        .when(githubClient)
        .getCommit(USER, REPO, BRANCH);

    // when
    AppVersionDto appVersion = service.getAppVersion();

    // then
    assertNotNull(appVersion);
    assertEquals("-", appVersion.getNumber());
    assertEquals("-", appVersion.getName());
    assertEquals("", appVersion.getDesc());
  }

  @Test
  public void getAppVersion_onlyDigestMessage() {
    // given
    String message = "123";
    doReturn(Optional.of(new CommitResponseDto(new CommitInfoDto(message))))
        .when(githubClient)
        .getCommit(USER, REPO, BRANCH);

    // when
    AppVersionDto appVersion = service.getAppVersion();

    // then
    assertNotNull(appVersion);
    assertEquals("-", appVersion.getNumber());
    assertEquals("-", appVersion.getName());
    assertEquals("", appVersion.getDesc());
  }

  @Test
  public void getAppVersion_onlyVersionMessage() {
    // given
    String message = "11.32.1";
    doReturn(Optional.of(new CommitResponseDto(new CommitInfoDto(message))))
        .when(githubClient)
        .getCommit(USER, REPO, BRANCH);

    // when
    AppVersionDto appVersion = service.getAppVersion();

    // then
    assertNotNull(appVersion);
    assertEquals("11.32.1", appVersion.getNumber());
    assertEquals("-", appVersion.getName());
    assertEquals("", appVersion.getDesc());
  }

  @Test
  public void getAppVersion_versionWithNameMessage() {
    // given
    String message = "11.32.1 New Version";
    doReturn(Optional.of(new CommitResponseDto(new CommitInfoDto(message))))
        .when(githubClient)
        .getCommit(USER, REPO, BRANCH);

    // when
    AppVersionDto appVersion = service.getAppVersion();

    // then
    assertNotNull(appVersion);
    assertEquals("11.32.1", appVersion.getNumber());
    assertEquals("New Version", appVersion.getName());
    assertEquals("", appVersion.getDesc());
  }

  @Test
  public void getAppVersion_versionWithNameWithDescMessage() {
    // given
    String message = "11.32.1 New Version\nFull Desc";
    doReturn(Optional.of(new CommitResponseDto(new CommitInfoDto(message))))
        .when(githubClient)
        .getCommit(USER, REPO, BRANCH);

    // when
    AppVersionDto appVersion = service.getAppVersion();

    // then
    assertNotNull(appVersion);
    assertEquals("11.32.1", appVersion.getNumber());
    assertEquals("New Version", appVersion.getName());
    assertEquals("Full Desc", appVersion.getDesc());
  }
}
