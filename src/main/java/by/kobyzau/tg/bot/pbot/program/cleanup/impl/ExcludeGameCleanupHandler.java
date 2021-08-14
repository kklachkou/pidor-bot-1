package by.kobyzau.tg.bot.pbot.program.cleanup.impl;

import by.kobyzau.tg.bot.pbot.model.ExcludeGameUserValue;
import by.kobyzau.tg.bot.pbot.program.cleanup.CleanupHandler;
import by.kobyzau.tg.bot.pbot.repository.exclude.ExcludeGameRepository;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ExcludeGameCleanupHandler implements CleanupHandler {

  @Autowired private ExcludeGameRepository excludeGameRepository;

  @Override
  public void cleanup() {
    LocalDate startDate = DateUtil.now().minusDays(2);
    excludeGameRepository.getAll().stream()
        .filter(d -> d.getLocalDate().isBefore(startDate))
        .map(ExcludeGameUserValue::getId)
        .forEach(excludeGameRepository::delete);
  }
}
