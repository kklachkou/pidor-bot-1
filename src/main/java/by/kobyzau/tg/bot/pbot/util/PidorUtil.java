package by.kobyzau.tg.bot.pbot.util;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;

public class PidorUtil {

  public static Optional<Integer> getTopPidorTgId(List<DailyPidor> dailyPidors) {
    if (CollectionUtil.isEmpty(dailyPidors)) {
      return Optional.empty();
    }
    Map<Integer, Integer> numWins = new HashMap<>();
    for (DailyPidor dailyPidor : dailyPidors) {
      Integer num = numWins.getOrDefault(dailyPidor.getPlayerTgId(), 0);
      numWins.put(dailyPidor.getPlayerTgId(), num + 1);
    }
    Integer maxWins = numWins.values().stream().max(Comparator.comparing(i -> i)).orElse(0);
    List<Integer> winners =
        numWins.entrySet().stream()
            .filter(e -> e.getValue().equals(maxWins))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    if (CollectionUtil.size(winners) == 1) {
      return Optional.of(winners.get(0));
    }
    LocalDate lastWin =
        dailyPidors.stream()
            .filter(d -> winners.contains(d.getPlayerTgId()))
            .map(DailyPidor::getLocalDate)
            .max(LocalDate::compareTo)
            .orElseThrow(IllegalStateException::new);

    return dailyPidors.stream()
        .filter(d -> lastWin.equals(d.getLocalDate()))
        .filter(d -> winners.contains(d.getPlayerTgId()))
        .findFirst()
        .map(DailyPidor::getPlayerTgId);
  }

  public static String getUserName(String message, int offset, int length) {
    return message.substring(offset + 1, offset + length);
  }
}
