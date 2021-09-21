package by.kobyzau.tg.bot.pbot.util;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class PidorUtil {

  public static final List<String> ANONYMOUS_NAMES =
      Arrays.asList(
          "Арнольд",
          "Бешенный бык",
          "Вульгарный енот",
          "Четкий Поцык",
          "Леопардо",
          "Невероятный пихалк",
          "Капинат Шмарвел",
          "Нетрезвый человек",
          "Монах в розовом",
          "Леу",
          "Кислый Билл",
          "Отчаянный домохозяец",
          "Красивый мужчина",
          "Солнышко",
          "Пчелка",
          "Черный Том",
          "Белый Том",
          "Дикий пёс",
          "Печенько",
          "Говнюк",
          "Дитчайший петух",
          "Свободный голубь",
          "Птенчик",
          "Адский наездник",
          "Аноним",
          "ЛОХнесское чудовище",
          "Человек-говнюк",
          "Ласточка",
          "Молодчик",
          "Украинец",
          "Белорус",
          "Русский",
          "Поляк",
          "Чех",
          "Британец",
          "Анонимус",
          "Не святой отец",
          "Всевидящий видящий",
          "Просто Тод");

  public static Optional<Long> getTopPidorTgId(List<DailyPidor> dailyPidors) {
    if (CollectionUtil.isEmpty(dailyPidors)) {
      return Optional.empty();
    }
    Map<Long, Integer> numWins = new HashMap<>();
    for (DailyPidor dailyPidor : dailyPidors) {
      Integer num = numWins.getOrDefault(dailyPidor.getPlayerTgId(), 0);
      numWins.put(dailyPidor.getPlayerTgId(), num + 1);
    }
    Integer maxWins = numWins.values().stream().max(Comparator.comparing(i -> i)).orElse(0);
    List<Long> winners =
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

  public static Map<Long, String> getAnonymousNames(List<Pidor> pidors, LocalDate date) {
    List<String> randomListByDay = CollectionUtil.getRandomListByDay(ANONYMOUS_NAMES, date);
    Map<Long, String> map = new HashMap<>();
    List<Long> ids = pidors.stream().map(Pidor::getId).sorted().collect(Collectors.toList());
    for (int i = 0; i < ids.size(); i++) {
      long id = ids.get(i);
      String name = CollectionUtil.getItem(randomListByDay, i);
      map.put(id, name);
    }
    return map;
  }
}
