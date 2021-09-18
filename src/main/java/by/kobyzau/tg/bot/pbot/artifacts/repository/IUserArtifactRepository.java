package by.kobyzau.tg.bot.pbot.artifacts.repository;

import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserArtifactRepository extends CrudRepository<UserArtifact, Long> {

  @Query("SELECT a FROM UserArtifact a WHERE a.chatId = ?1")
  List<UserArtifact> findByChatId(long chatId);
}
