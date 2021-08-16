package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.PidorYearlyStat;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.yearlystat.PidorYearlyStatRepository;
import by.kobyzau.tg.bot.pbot.service.PidorYearlyStatService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PidorYearlyStatServiceImpl implements PidorYearlyStatService {

  @Autowired private PidorYearlyStatRepository repository;
  @Autowired private DailyPidorRepository dailyPidorRepository;

  @Override
  public Optional<PidorYearlyStat> getStat(long chatId, long userId, int year) {
    return repository.getByChatId(chatId).stream()
        .filter(s -> s.getYear() == year)
        .filter(s -> s.getPlayerTgId() == userId)
        .findFirst();
  }

  @Override
  public void createYearlyStat(long chatId, int year) {

    List<DailyPidor> dailyPidors =
        dailyPidorRepository.getByChat(chatId).stream()
            .filter(d -> d.getLocalDate().getYear() == year)
            .collect(Collectors.toList());
    Set<Long> pidorIds =
        dailyPidors.stream().map(DailyPidor::getPlayerTgId).collect(Collectors.toSet());
    for (Long pidorId : pidorIds) {
      int count = (int) dailyPidors.stream().filter(d -> d.getPlayerTgId() == pidorId).count();
      LocalDate lastDate =
          dailyPidors.stream()
              .filter(d -> d.getPlayerTgId() == pidorId)
              .max(Comparator.comparing(DailyPidor::getLocalDate))
              .map(DailyPidor::getLocalDate)
              .orElseGet(() -> LocalDate.of(year, 1, 1));
      PidorYearlyStat stat = new PidorYearlyStat();
      stat.setChatId(chatId);
      stat.setPlayerTgId(pidorId);
      stat.setYear(year);
      stat.setCount(count);
      stat.setLastDate(lastDate);
      repository.create(stat);
    }
  }
}
