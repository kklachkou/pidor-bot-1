package by.kobyzau.tg.bot.pbot.program.cleanup.impl;

import by.kobyzau.tg.bot.pbot.model.CustomDailyUserData;
import by.kobyzau.tg.bot.pbot.program.cleanup.CleanupHandler;
import by.kobyzau.tg.bot.pbot.repository.custom.CustomDailyDataRepository;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static by.kobyzau.tg.bot.pbot.model.CustomDailyUserData.Type.ELECTION_VOTE;
import static by.kobyzau.tg.bot.pbot.model.CustomDailyUserData.Type.FUTURE_ACTION;

@Component
public class CustomDailyUserDataCleanupHandler implements CleanupHandler {

  @Autowired private CustomDailyDataRepository dailyDataRepository;

  private final Set<CustomDailyUserData.Type> cleanupTypes =
      new HashSet<>(Arrays.asList(ELECTION_VOTE, FUTURE_ACTION));

  @Override
  public void cleanup() {
    LocalDate startDate = DateUtil.now().minusDays(2);
    dailyDataRepository.getAll().stream()
        .filter(d -> cleanupTypes.contains(d.getType()))
        .filter(d -> d.getLocalDate().isBefore(startDate))
        .map(CustomDailyUserData::getId)
        .forEach(dailyDataRepository::delete);
  }
}
