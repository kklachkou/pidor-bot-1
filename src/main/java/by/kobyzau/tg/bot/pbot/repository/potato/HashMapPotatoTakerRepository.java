package by.kobyzau.tg.bot.pbot.repository.potato;

import by.kobyzau.tg.bot.pbot.model.HotPotatoTaker;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class HashMapPotatoTakerRepository implements PotatoTakerRepository {

  private final Map<Long, HotPotatoTaker> map = new HashMap<>();

  @Override
  public List<HotPotatoTaker> getByChatAndDate(long chatId, LocalDate date) {
    return getAll().stream()
        .filter(p -> p.getChatId() == chatId)
        .filter(p -> p.getDate().equals(date))
        .collect(Collectors.toList());
  }

  @Override
  public long create(HotPotatoTaker obj) {
    long newId = getNewId();
    obj.setId(newId);
    map.put(newId, obj);
    return newId;
  }

  @Override
  public void update(HotPotatoTaker obj) {
    map.put(obj.getId(), obj);
  }

  @Override
  public HotPotatoTaker get(long id) {
    return map.get(id);
  }

  @Override
  public List<HotPotatoTaker> getAll() {
    return map.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
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
