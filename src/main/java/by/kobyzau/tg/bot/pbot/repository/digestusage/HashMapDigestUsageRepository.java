package by.kobyzau.tg.bot.pbot.repository.digestusage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import by.kobyzau.tg.bot.pbot.model.DigestUsage;
import by.kobyzau.tg.bot.pbot.util.StringUtil;

@Component
@Profile("dev")
public class HashMapDigestUsageRepository implements DigestUsageRepository {
  private final Map<Long, DigestUsage> map = new HashMap<>();

  @Override
  public List<DigestUsage> findByType(String type) {
    return getAll().stream()
        .filter(d -> StringUtil.equals(type, d.getType()))
        .collect(Collectors.toList());
  }

  @Override
  public long create(DigestUsage obj) {
    long newId = getNewId();
    obj.setId(newId);
    map.put(newId, obj);
    return newId;
  }

  @Override
  public void update(DigestUsage obj) {
    map.put(obj.getId(), obj);
  }

  @Override
  public DigestUsage get(long id) {
    return map.get(id);
  }

  @Override
  public List<DigestUsage> getAll() {
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
