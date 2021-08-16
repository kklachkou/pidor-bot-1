package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorStatus;
import by.kobyzau.tg.bot.pbot.model.PidorYearlyStat;
import by.kobyzau.tg.bot.pbot.model.PidotStatusPosition;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.PidorStatusService;
import by.kobyzau.tg.bot.pbot.service.PidorYearlyStatService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PidorStatusServiceImpl implements PidorStatusService {

  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private PidorService pidorService;
  @Autowired private PidorYearlyStatService pidorYearlyStatService;

  @Override
  public PidorStatus getPidorStatus(long chatId, int year) {
    final List<Pidor> pidors = pidorService.getByChat(chatId);
    final List<DailyPidor> dailyPidors =
        dailyPidorRepository.getByChat(chatId).stream()
            .filter(d -> d.getLocalDate().getYear() == year)
            .collect(Collectors.toList());
    List<Pair<Pidor, Integer>> simpleStatus =
        pidors.stream()
            .map(p -> new Pair<>(p, getNumWins(p, dailyPidors, year)))
            .sorted(Comparator.comparingInt(Pair::getRight))
            .collect(Collectors.toList());
    Map<Integer, Set<Pidor>> pidorTop = new TreeMap<>((o1, o2) -> Integer.compare(o2, o1));
    for (Pair<Pidor, Integer> status : simpleStatus) {
      Set<Pidor> set = pidorTop.getOrDefault(status.getRight(), new HashSet<>());
      set.add(status.getLeft());
      pidorTop.put(status.getRight(), set);
    }
    List<PidotStatusPosition> pidotStatusPositions = new ArrayList<>();
    for (Map.Entry<Integer, Set<Pidor>> entry : pidorTop.entrySet()) {
      PidotStatusPosition pidotStatusPosition = new PidotStatusPosition();
      int numWins = entry.getKey();
      pidotStatusPosition.setNumWins(numWins);
      Set<Pidor> localPidors = entry.getValue();
      if (numWins == 0) {
        pidotStatusPosition.setSecondaryPidors(new ArrayList<>(localPidors));
      } else {
        Pidor primaryPidor = getPrimaryPidor(localPidors, dailyPidors, year);
        pidotStatusPosition.setPrimaryPidor(primaryPidor);
        pidotStatusPosition.setSecondaryPidors(getSecondaryPidors(localPidors, primaryPidor));
      }
      pidotStatusPositions.add(pidotStatusPosition);
    }
    PidorStatus result = new PidorStatus();
    result.setPidorStatusPositions(pidotStatusPositions);
    return result;
  }

  private int getNumWins(Pidor pidor, List<DailyPidor> dailyPidors, int year) {
    long tgId = pidor.getTgId();
    Optional<PidorYearlyStat> yearlyStat =
        pidorYearlyStatService.getStat(pidor.getChatId(), tgId, year);
    return yearlyStat
        .map(PidorYearlyStat::getCount)
        .orElseGet(() -> (int) dailyPidors.stream().filter(d -> d.getPlayerTgId() == tgId).count());
  }

  private Pidor getPrimaryPidor(Set<Pidor> pidors, List<DailyPidor> dailyPidors, int year) {
    Pidor primaryPidor = null;
    LocalDate primaryPidorLastDate = LocalDate.MIN;
    for (Pidor pidor : pidors) {
      LocalDate lastPidorDate = getLastPidorDate(pidor, dailyPidors, year);
      if (primaryPidor == null) {
        primaryPidor = pidor;
        primaryPidorLastDate = lastPidorDate;
        continue;
      }
      if (lastPidorDate.isAfter(primaryPidorLastDate)) {
        primaryPidor = pidor;
        primaryPidorLastDate = lastPidorDate;
      }
    }
    return primaryPidor;
  }

  private LocalDate getLastPidorDate(Pidor pidor, List<DailyPidor> dailyPidors, int year) {
    Optional<PidorYearlyStat> stat = pidorYearlyStatService.getStat(pidor.getChatId(), pidor.getTgId(), year);
    if (stat.isPresent()) {
      return stat.get().getLastDate();
    }
    return dailyPidors.stream()
        .filter(d -> d.getPlayerTgId() == pidor.getTgId())
        .map(DailyPidor::getLocalDate)
        .filter(Objects::nonNull)
        .max(Comparator.naturalOrder())
        .orElse(LocalDate.MIN);
  }

  private List<Pidor> getSecondaryPidors(Set<Pidor> pidors, Pidor primaryPidor) {
    return pidors.stream()
        .filter(p -> p.getId() != primaryPidor.getId())
        .collect(Collectors.toList());
  }
}
