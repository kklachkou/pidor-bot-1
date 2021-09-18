package by.kobyzau.tg.bot.pbot.artifacts.repository;

import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;

import java.util.List;

public interface UserArtifactRepository extends CrudRepository<UserArtifact> {

  List<UserArtifact> getByChatId(long chatId);
}
