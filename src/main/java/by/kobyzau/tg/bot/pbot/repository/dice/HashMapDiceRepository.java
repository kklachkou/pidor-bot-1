package by.kobyzau.tg.bot.pbot.repository.dice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import by.kobyzau.tg.bot.pbot.model.PidorDice;

@Component
@Profile("dev")
public class HashMapDiceRepository implements DiceRepository {

  private final Map<Long, PidorDice> map = new HashMap<>();

  @Override
  public long create(PidorDice obj) {
    long newId = getNewId();
    obj.setId(newId);
    map.put(newId, obj);
    return newId;
  }

  @Override
  public void update(PidorDice obj) {
    map.put(obj.getId(), obj);
  }

  @Override
  public PidorDice get(long id) {
    return map.get(id);
  }

  @Override
  public List<PidorDice> getAll() {
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
