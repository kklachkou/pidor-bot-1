package by.kobyzau.tg.bot.pbot.repository.pidorofyear;

import by.kobyzau.tg.bot.pbot.model.PidorOfYear;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Profile("dev")
public class HashMapPidorOfYearRepository implements PidorOfYearRepository {

  private final Map<Long, PidorOfYear> map = new HashMap<>();

  @Override
  public long create(PidorOfYear obj) {
    long newId = getNewId();
    map.put(newId, obj);
    return newId;
  }

  @Override
  public void update(PidorOfYear obj) {
    map.put(obj.getId(), obj);
  }

  @Override
  public PidorOfYear get(long id) {
    return map.get(id);
  }

  @Override
  public List<PidorOfYear> getAll() {
    return map.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Override
  public List<PidorOfYear> getPidorOfYearByChat(long chatId) {
    return getAll().stream().filter(p -> p.getChatId() == chatId).collect(Collectors.toList());
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
