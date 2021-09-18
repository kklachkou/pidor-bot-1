package by.kobyzau.tg.bot.pbot.artifacts.repository;

import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Profile("prod")
public class DBArtifactRepository implements UserArtifactRepository {

  @Autowired private IUserArtifactRepository repository;

  @Override
  public List<UserArtifact> getByChatId(long chatId) {
    return repository.findByChatId(chatId);
  }

  @Override
  public long create(UserArtifact obj) {
    return repository.save(obj).getId();
  }

  @Override
  public void update(UserArtifact obj) {
    repository.save(obj);
  }

  @Override
  public UserArtifact get(long id) {
    return repository.findById(id).orElse(null);
  }

  @Override
  public List<UserArtifact> getAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    repository.deleteById(id);
  }
}
