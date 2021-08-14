package by.kobyzau.tg.bot.pbot.program.cleanup.impl;

import by.kobyzau.tg.bot.pbot.model.HotPotatoTaker;
import by.kobyzau.tg.bot.pbot.program.cleanup.CleanupHandler;
import by.kobyzau.tg.bot.pbot.repository.potato.PotatoTakerRepository;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HotPotatoCleanupHandler implements CleanupHandler {

  @Autowired private PotatoTakerRepository repository;

  @Override
  public void cleanup() {
    LocalDate startDate = DateUtil.now().minusDays(2);
    repository.getAll().stream()
        .filter(d -> d.getDate().isBefore(startDate))
        .map(HotPotatoTaker::getId)
        .forEach(repository::delete);
  }
}
