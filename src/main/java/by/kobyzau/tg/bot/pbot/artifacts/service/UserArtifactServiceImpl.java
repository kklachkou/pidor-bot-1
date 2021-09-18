package by.kobyzau.tg.bot.pbot.artifacts.service;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import by.kobyzau.tg.bot.pbot.artifacts.repository.UserArtifactRepository;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserArtifactServiceImpl implements UserArtifactService {
  private final UserArtifactRepository userArtifactRepository;

  @Override
  public void addArtifact(long chatId, long userId, ArtifactType artifactType, LocalDate date) {
    userArtifactRepository.create(
        UserArtifact.builder()
            .chatId(chatId)
            .userId(userId)
            .artifactType(artifactType)
            .date(date)
            .build());
  }

  @Override
  public Optional<UserArtifact> getUserArtifact(
      long chatId, long userId, ArtifactType artifactType, LocalDate date) {
    return userArtifactRepository.getByChatId(chatId).stream()
        .filter(a -> a.getDate().equals(date))
        .filter(a -> a.getUserId() == userId)
        .filter(a -> a.getArtifactType() == artifactType)
        .findFirst();
  }

  @Override
  public List<UserArtifact> getUserArtifacts(long chatId, LocalDate date) {
    return userArtifactRepository.getByChatId(chatId).stream()
        .filter(a -> a.getDate().equals(date))
        .collect(Collectors.toList());
  }

  @Override
  public void deleteArtifact(long artifactId) {
    userArtifactRepository.delete(artifactId);
  }

  @Override
  public void clearUserArtifacts(long chatId) {
    LocalDate now = DateUtil.now();
    userArtifactRepository.getByChatId(chatId).stream()
        .filter(a -> !a.getDate().isAfter(now))
        .map(UserArtifact::getId)
        .forEach(userArtifactRepository::delete);
  }

  @Override
  public List<UserArtifact> getUserArtifacts(long chatId, long userId) {
    return userArtifactRepository.getByChatId(chatId).stream()
        .filter(a -> a.getUserId() == userId)
        .collect(Collectors.toList());
  }
}
