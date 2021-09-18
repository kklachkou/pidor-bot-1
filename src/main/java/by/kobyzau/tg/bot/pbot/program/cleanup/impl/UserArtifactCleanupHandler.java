package by.kobyzau.tg.bot.pbot.program.cleanup.impl;

import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import by.kobyzau.tg.bot.pbot.artifacts.repository.UserArtifactRepository;
import by.kobyzau.tg.bot.pbot.program.cleanup.CleanupHandler;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class UserArtifactCleanupHandler implements CleanupHandler {

  private final UserArtifactRepository userArtifactRepository;

  @Override
  public void cleanup() {
    LocalDate cleanupDate = DateUtil.now().minusDays(2);
    userArtifactRepository.getAll().stream()
        .filter(a -> a.getDate().isBefore(cleanupDate))
        .map(UserArtifact::getId)
        .forEach(userArtifactRepository::delete);
  }
}
