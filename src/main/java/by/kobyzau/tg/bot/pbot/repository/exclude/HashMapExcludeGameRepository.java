package by.kobyzau.tg.bot.pbot.repository.exclude;

import by.kobyzau.tg.bot.pbot.model.ExcludeGameUserValue;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Profile({"dev", "integration-test"})
public class HashMapExcludeGameRepository implements ExcludeGameRepository {

  private final Map<Long, ExcludeGameUserValue> map = new HashMap<>();

  @Override
  public List<ExcludeGameUserValue> getByChatId(long chatId) {
    return getAll().stream().filter(c -> c.getChatId() == chatId).collect(Collectors.toList());
  }

  @Override
  public long create(ExcludeGameUserValue obj) {
    long newId = getNewId();
    obj.setId(newId);
    map.put(newId, obj);
    return newId;
  }

  @Override
  public void update(ExcludeGameUserValue obj) {
    map.put(obj.getId(), obj);
  }

  @Override
  public ExcludeGameUserValue get(long id) {
    return map.get(id);
  }

  @Override
  public List<ExcludeGameUserValue> getAll() {
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
