package by.kobyzau.tg.bot.pbot.service.pidor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PidorOfDayServiceFactory {

  @Autowired private List<PidorOfDayService> services;

  private Map<PidorOfDayService.Type, PidorOfDayService> map;

  @PostConstruct
  private void init() {
    map = new HashMap<>();
    services.forEach(h -> map.put(h.getType(), h));
  }

  public PidorOfDayService getService(PidorOfDayService.Type type) {
    return map.get(type);
  }
}
