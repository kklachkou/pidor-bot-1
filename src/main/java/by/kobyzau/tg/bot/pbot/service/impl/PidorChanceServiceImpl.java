package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.HashMapPidorOfDayRepository;
import by.kobyzau.tg.bot.pbot.service.PidorChanceService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.PidorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class PidorChanceServiceImpl implements PidorChanceService {

  private final int totalYearsNum;

  private final PidorService pidorService;

  private final DailyPidorRepository dailyPidorRepository;

  private final BotActionCollector botActionCollector;

  private final Supplier<LocalDate> nowProvider;

  @Autowired
  private PidorChanceServiceImpl(
      PidorService pidorService,
      DailyPidorRepository dailyPidorRepository,
      BotActionCollector botActionCollector) {
    this(
        pidorService,
        dailyPidorRepository,
        botActionCollector,
        DateUtil::now,
        10_000);
  }

  public PidorChanceServiceImpl(
          PidorService pidorService,
          DailyPidorRepository dailyPidorRepository,
          BotActionCollector botActionCollector,
          Supplier<LocalDate> nowProvider,
          int totalYearsNum) {
    this.pidorService = pidorService;
    this.dailyPidorRepository = dailyPidorRepository;
    this.botActionCollector = botActionCollector;
    this.nowProvider = nowProvider;
    this.totalYearsNum = totalYearsNum;
  }

  @Override
  public List<Pair<Pidor, Double>> calcChances(long chatId, int year) {
    List<Pidor> pidors = getPidors(chatId);
    List<DailyPidor> dailyPidors =
        dailyPidorRepository.getByChat(chatId).stream()
            .filter(dailyPidor -> dailyPidor.getLocalDate().getYear() == year)
            .collect(Collectors.toList());
    Map<Pidor, Integer> numWins = new HashMap<>();
    for (int iteration = 0; iteration < totalYearsNum; iteration++) {
      if (iteration % 1000 == 0) {
        botActionCollector.typing(chatId);
        Thread.yield();
      }
      DailyPidorRepository repository = getNewRepository(dailyPidors);
      findAllDailyPidors(chatId, repository, pidors);
      Pidor pidorOfTheYear = getPidorOfTheYear(chatId, repository, pidors);
      int wins = numWins.getOrDefault(pidorOfTheYear, 0);
      numWins.put(pidorOfTheYear, wins + 1);
    }
    List<Pair<Pidor, Double>> list = new ArrayList<>();
    for (Pidor pidor : pidors) {
      list.add(new Pair<>(pidor, getPercent(numWins.getOrDefault(pidor, 0))));
    }
    return list.stream()
        .sorted(Collections.reverseOrder(Comparator.comparing(Pair::getRight)))
        .collect(Collectors.toList());
  }

  private DailyPidorRepository getNewRepository(List<DailyPidor> allRealDailyPidors) {
    DailyPidorRepository memoryDailyRepo = new HashMapPidorOfDayRepository();
    allRealDailyPidors.forEach(memoryDailyRepo::create);
    return memoryDailyRepo;
  }

  private void findAllDailyPidors(
      long chatId, DailyPidorRepository repository, List<Pidor> pidors) {
    LocalDate date = nowProvider.get();
    final int year = date.getYear();
    if (getDailyPidorByDate(chatId, repository, date).isPresent()) {
      date = date.plusDays(1);
    }
    while (date.getYear() == year) {
      Pidor pidor = CollectionUtil.getRandomValue(pidors);
      repository.create(new DailyPidor(pidor.getTgId(), pidor.getChatId(), date));
      date = date.plusDays(1);
    }
  }

  private Pidor getPidorOfTheYear(
      long chatId, DailyPidorRepository repository, List<Pidor> pidors) {
    int year = nowProvider.get().getYear();
    Optional<Integer> pidorOfTheYearId =
        PidorUtil.getTopPidorTgId(
            repository.getByChat(chatId).stream()
                .filter(dailyPidor -> dailyPidor.getLocalDate().getYear() == year)
                .collect(Collectors.toList()));
    return pidors.stream()
        .filter(p -> p.getTgId() == pidorOfTheYearId.orElse(-1))
        .findFirst()
        .orElseThrow(IllegalAccessError::new);
  }

  private Optional<DailyPidor> getDailyPidorByDate(
      long chatId, DailyPidorRepository dailyPidorRepository, LocalDate localDate) {
    return dailyPidorRepository.getByChatAndDate(chatId, localDate);
  }

  private List<Pidor> getPidors(long chatId) {
    return pidorService.getByChat(chatId).stream()
        .distinct()
        .collect(Collectors.toList());
  }

  private double getPercent(int numWins) {
    return (double)numWins * 100 / totalYearsNum;
  }
}
