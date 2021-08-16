package by.kobyzau.tg.bot.pbot.program.cleanup.impl;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.PidorYearlyStat;
import by.kobyzau.tg.bot.pbot.program.cleanup.CleanupHandler;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.repository.yearlystat.PidorYearlyStatRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PidorYearlyCleanupHandler implements CleanupHandler {
  @Autowired private PidorYearlyStatRepository pidorYearlyStatRepository;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private PidorRepository pidorRepository;

  @Override
  public void cleanup() {
    cleanupStat();
    cleanupPrevDaily();
  }

  private void cleanupPrevDaily() {
    List<PidorYearlyStat> yearlyStats = pidorYearlyStatRepository.getAll();
    for (PidorYearlyStat yearlyStat : yearlyStats) {
      int year = yearlyStat.getYear();
      long chatId = yearlyStat.getChatId();
      long userId = yearlyStat.getPlayerTgId();
      dailyPidorRepository.getByChat(chatId).stream()
          .filter(d -> d.getPlayerTgId() == userId)
          .filter(d -> d.getLocalDate().getYear() == year)
          .map(DailyPidor::getId)
          .forEach(dailyPidorRepository::delete);
    }
  }

  private void cleanupStat() {
    List<PidorYearlyStat> yearlyStats = pidorYearlyStatRepository.getAll();
    for (PidorYearlyStat yearlyStat : yearlyStats) {
      if (!hasDaily(yearlyStat.getChatId(), yearlyStat.getPlayerTgId())
          && !hasPidor(yearlyStat.getChatId(), yearlyStat.getPlayerTgId())) {
        pidorYearlyStatRepository.delete(yearlyStat.getId());
      }
    }
  }

  private boolean hasDaily(long chatId, long userId) {
    return dailyPidorRepository.getByChat(chatId).stream()
        .anyMatch(d -> d.getPlayerTgId() == userId);
  }

  private boolean hasPidor(long chatId, long userId) {
    return pidorRepository.getByChatAndPlayerTgId(chatId, userId).isPresent();
  }
}
