package by.kobyzau.tg.bot.pbot.repository.dailypidor;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Profile("dev")
public class HashMapPidorOfDayRepository implements DailyPidorRepository {

  private final Map<Long, DailyPidor> map = new HashMap<>();

  @Override
  public List<DailyPidor> getByChat(long chatId) {
    return getAll().stream()
        .filter(p -> chatId == p.getChatId())
        .sorted(Comparator.comparing(DailyPidor::getLocalDate))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<DailyPidor> getByChatAndDate(long chatId, LocalDate localDate) {
    return getAll().stream()
        .filter(p -> chatId == p.getChatId())
        .filter(p -> localDate.equals(p.getLocalDate()))
        .findFirst();
  }

  @Override
  public long create(DailyPidor obj) {
    long newId = getNewId();
    obj.setId(newId);
    map.put(newId, obj);
    return newId;
  }

  @Override
  public void update(DailyPidor obj) {
    map.put(obj.getId(), obj);
  }

  @Override
  public DailyPidor get(long id) {
    return map.get(id);
  }

  @Override
  public List<DailyPidor> getAll() {
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
