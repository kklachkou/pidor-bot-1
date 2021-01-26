package by.kobyzau.tg.bot.pbot.repository.custom;

import by.kobyzau.tg.bot.pbot.model.CustomDailyUserData;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Profile({"dev", "integration-test"})
public class HashMapDailyDataRepositoryCustom implements CustomDailyDataRepository {

  private final Map<Long, CustomDailyUserData> map = new HashMap<>();

  @Override
  public List<CustomDailyUserData> getByChat(long chatId) {
    return getAll().stream()
        .filter(p -> chatId == p.getChatId())
        .sorted(Comparator.comparing(CustomDailyUserData::getLocalDate))
        .collect(Collectors.toList());
  }

  @Override
  public List<CustomDailyUserData> getByChatAndDate(long chatId, LocalDate localDate) {
    return getAll().stream()
        .filter(p -> chatId == p.getChatId())
        .filter(p -> localDate.equals(p.getLocalDate()))
        .collect(Collectors.toList());
  }

  @Override
  public long create(CustomDailyUserData obj) {
    long newId = getNewId();
    obj.setId(newId);
    map.put(newId, obj);
    return newId;
  }

  @Override
  public void update(CustomDailyUserData obj) {
    map.put(obj.getId(), obj);
  }

  @Override
  public CustomDailyUserData get(long id) {
    return map.get(id);
  }

  @Override
  public List<CustomDailyUserData> getAll() {
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
