package by.kobyzau.tg.bot.pbot.handlers.stat;

import by.kobyzau.tg.bot.pbot.model.StatType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class StatHandlerFactory {

  @Autowired private List<StatHandler> handlers;

  private Map<StatType, StatHandler> map;

  @PostConstruct
  private void init() {
    map = new HashMap<>();
    handlers.forEach(h -> map.put(h.getType(), h));
  }

  public StatHandler getHandler(StatType type) {
    return Optional.ofNullable(map.get(type))
        .orElseThrow(() -> new IllegalStateException("Unsupported type: " + type));
  }
}
