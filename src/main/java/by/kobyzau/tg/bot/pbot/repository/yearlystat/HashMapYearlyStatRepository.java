package by.kobyzau.tg.bot.pbot.repository.yearlystat;

import by.kobyzau.tg.bot.pbot.model.PidorYearlyStat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class HashMapYearlyStatRepository implements PidorYearlyStatRepository {
  private final Map<Long, PidorYearlyStat> map = new HashMap<>();

  @Override
  public List<PidorYearlyStat> getByChatId(long chatId) {
    return map.values().stream()
        .filter(Objects::nonNull)
        .filter(f -> f.getChatId() == chatId)
        .collect(Collectors.toList());
  }

  @Override
  public long create(PidorYearlyStat obj) {
    long newId = getNewId();
    obj.setId(newId);
    map.put(newId, obj);
    return newId;
  }

  @Override
  public void update(PidorYearlyStat obj) {
    map.put(obj.getId(), obj);
  }

  @Override
  public PidorYearlyStat get(long id) {
    return map.get(id);
  }

  @Override
  public List<PidorYearlyStat> getAll() {
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
