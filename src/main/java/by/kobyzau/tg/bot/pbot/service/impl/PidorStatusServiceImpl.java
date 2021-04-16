package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.model.*;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.PidorStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PidorStatusServiceImpl implements PidorStatusService {

  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private PidorService pidorService;

  @Override
  public PidorStatus getPidorStatus(long chatId, int year) {
    final List<Pidor> pidors = pidorService.getByChat(chatId);
    final List<DailyPidor> dailyPidors =
        dailyPidorRepository.getByChat(chatId).stream()
            .filter(d -> d.getLocalDate().getYear() == year)
            .collect(Collectors.toList());
    List<Pair<Pidor, Integer>> simpleStatus =
        pidors.stream()
            .map(p -> new Pair<>(p, getNumWins(p, dailyPidors)))
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
        Pidor primaryPidor = getPrimaryPidor(localPidors, dailyPidors);
        pidotStatusPosition.setPrimaryPidor(primaryPidor);
        pidotStatusPosition.setSecondaryPidors(getSecondaryPidors(localPidors, primaryPidor));
      }
      pidotStatusPositions.add(pidotStatusPosition);
    }
    PidorStatus result = new PidorStatus();
    result.setPidorStatusPositions(pidotStatusPositions);
    return result;
  }

  private int getNumWins(Pidor pidor, List<DailyPidor> dailyPidors) {
    long tgId = pidor.getTgId();
    return (int) dailyPidors.stream().filter(d -> d.getPlayerTgId() == tgId).count();
  }

  private Pidor getPrimaryPidor(Set<Pidor> pidors, List<DailyPidor> dailyPidors) {
    Pidor primaryPidor = null;
    LocalDate primaryPidorLastDate = LocalDate.MIN;
    for (Pidor pidor : pidors) {
      LocalDate lastPidorDate = getLastPidorDate(pidor, dailyPidors);
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

  private LocalDate getLastPidorDate(Pidor pidor, List<DailyPidor> dailyPidors) {
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
