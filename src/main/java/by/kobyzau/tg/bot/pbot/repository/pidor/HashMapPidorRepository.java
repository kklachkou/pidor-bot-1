package by.kobyzau.tg.bot.pbot.repository.pidor;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component

@Profile("dev")
public class HashMapPidorRepository implements PidorRepository {

  private final Map<Long, Pidor> map = new HashMap<>();

  @Override
  public long create(Pidor obj) {
    long newId = getNewId();
    map.put(newId, obj);
    return newId;
  }

  @Override
  public List<Pidor> getByChat(long chatId) {
    return getAll().stream().filter(p -> chatId == p.getChatId()).collect(Collectors.toList());
  }

  @Override
  public Optional<Pidor> getByChatAndPlayerTgId(long chatId, int tgId) {
    return getAll().stream()
        .filter(p -> tgId == p.getTgId())
        .filter(p -> chatId == p.getChatId())
        .findFirst();
  }

  @Override
  public void update(Pidor obj) {
    map.put(obj.getId(), obj);
  }

  @Override
  public Pidor get(long id) {
    return map.get(id);
  }

  @Override
  public List<Pidor> getAll() {
    return map.values().stream().filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    map.put(id, null);
  }

  private synchronized long getNewId() {
    long newId = map.keySet().stream().max(Long::compareTo).orElse(1L) + 1;
    map.put(newId, null);
    return newId;
  }
}
