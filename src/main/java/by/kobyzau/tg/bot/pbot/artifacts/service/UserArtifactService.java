package by.kobyzau.tg.bot.pbot.artifacts.service;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserArtifactService {

  void addArtifact(long chatId, long userId, ArtifactType artifactType, LocalDate date);

  void deleteArtifact(long artifactId);

  List<UserArtifact> getUserArtifacts(long chatId, long userId);

  List<UserArtifact> getUserArtifacts(long chatId);

  Optional<UserArtifact> getUserArtifact(long chatId, long userId, ArtifactType artifactType);

  void clearUserArtifacts(long chatId, ArtifactType artifactType);
}
