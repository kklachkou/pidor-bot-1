package by.kobyzau.tg.bot.pbot.handlers.future;

import by.kobyzau.tg.bot.pbot.service.FutureActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class FutureActionHandlerFactory {

  @Autowired private List<FutureActionHandler> handlers;

  private Map<FutureActionService.FutureActionType, FutureActionHandler> map;

  @PostConstruct
  private void init() {
    map = new HashMap<>();
    handlers.forEach(h -> map.put(h.getType(), h));
  }

  public Optional<FutureActionHandler> getHandler(FutureActionService.FutureActionType type) {
    return Optional.ofNullable(map.get(type));
  }
}
