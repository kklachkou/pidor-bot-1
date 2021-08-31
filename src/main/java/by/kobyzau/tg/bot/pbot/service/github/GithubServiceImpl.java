package by.kobyzau.tg.bot.pbot.service.github;

import by.kobyzau.tg.bot.pbot.client.GithubClient;
import by.kobyzau.tg.bot.pbot.model.api.github.CommitInfoDto;
import by.kobyzau.tg.bot.pbot.model.api.github.CommitResponseDto;
import by.kobyzau.tg.bot.pbot.model.dto.AppVersionDto;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GithubServiceImpl implements GithubService {
  @Value("${api.github.user}")
  private String user;

  @Value("${api.github.repo}")
  private String repositoryName;

  @Value("${api.github.branch}")
  private String branchName;

  private final GithubClient githubClient;

  private static final Pattern COMMIT_PATTERN =
      Pattern.compile("^(\\d+\\.\\d+\\.\\d+) ?(.+)?\n?(.+)?$");

  @Override
  public AppVersionDto getAppVersion() {
    Optional<String> commitMessage =
        githubClient
            .getCommit(user, repositoryName, branchName)
            .map(CommitResponseDto::getCommit)
            .map(CommitInfoDto::getMessage);
    AppVersionDto.AppVersionDtoBuilder appVersionBuilder =
        AppVersionDto.builder().number("-").name("-").desc("");
    if (!commitMessage.isPresent()) {
      return appVersionBuilder.build();
    }
    Matcher matcher = COMMIT_PATTERN.matcher(commitMessage.get());
    if (matcher.matches()) {
      switch (matcher.groupCount()) {
        case 3:
          appVersionBuilder.desc(StringUtil.isBlank(matcher.group(3), ""));
        case 2:
          appVersionBuilder.name(StringUtil.isBlank(matcher.group(2), "-"));
        case 1:
          appVersionBuilder.number(StringUtil.isBlank(matcher.group(1), "-"));
      }
    }
    return appVersionBuilder.build();
  }
}
