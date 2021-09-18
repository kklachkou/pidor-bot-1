package by.kobyzau.tg.bot.pbot.artifacts.repository;

import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Profile({"dev", "integration-test"})
public class HashMapUserArtifactRepository implements UserArtifactRepository {

  private final Map<Long, UserArtifact> map = new HashMap<>();

  @Override
  public List<UserArtifact> getByChatId(long chatId) {
    return getAll().stream().filter(d -> d.getChatId() == chatId).collect(Collectors.toList());
  }

  @Override
  public long create(UserArtifact obj) {
    long newId = getNewId();
    obj.setId(newId);
    map.put(newId, obj);
    return newId;
  }

  @Override
  public void update(UserArtifact obj) {
    map.put(obj.getId(), obj);
  }

  @Override
  public UserArtifact get(long id) {
    return map.get(id);
  }

  @Override
  public List<UserArtifact> getAll() {
    return new ArrayList<>(map.values());
  }

  @Override
  public void delete(long id) {
    map.remove(id);
  }

  private synchronized long getNewId() {
    long newId = map.keySet().stream().max(Long::compareTo).orElse(1L) + 1;
    map.put(newId, null);
    return newId;
  }
}
