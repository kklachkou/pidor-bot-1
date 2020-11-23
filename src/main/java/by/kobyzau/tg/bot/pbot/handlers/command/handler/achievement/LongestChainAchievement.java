package by.kobyzau.tg.bot.pbot.handlers.command.handler.achievement;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class LongestChainAchievement implements Achievement {

  private final PidorService pidorService;

  private final DailyPidorRepository dailyPidorRepository;

  @Autowired
  public LongestChainAchievement(
      PidorService pidorService, DailyPidorRepository dailyPidorRepository) {
    this.pidorService = pidorService;
    this.dailyPidorRepository = dailyPidorRepository;
  }

  @Override
  public Optional<Text> getAchievementInfo(long chatId) {
    int startYear = 2020;
    List<DailyPidor> dailyPidors =
        dailyPidorRepository.getByChat(chatId).stream()
            .filter(dp -> dp.getLocalDate() != null)
            .filter(dp -> dp.getLocalDate().getYear() >= startYear)
            .sorted(Comparator.comparing(DailyPidor::getLocalDate))
            .collect(Collectors.toList());
    if (dailyPidors.isEmpty()) {
      return Optional.empty();
    }
    Map<Integer, Integer> topChainsByPidorId = new HashMap<>();
    int currentChainNum = 0;
    OptionalInt currentChainUserId = OptionalInt.empty();
    for (DailyPidor dailyPidor : dailyPidors) {
      if (!currentChainUserId.isPresent()) {
        currentChainNum = 1;
        currentChainUserId = OptionalInt.of(dailyPidor.getPlayerTgId());
        continue;
      }
      int pidorId = currentChainUserId.getAsInt();
      if (dailyPidor.getPlayerTgId() == pidorId) {
        currentChainNum++;
        continue;
      }
      int topChain = topChainsByPidorId.getOrDefault(pidorId, 0);
      if (currentChainNum > topChain) {
        topChainsByPidorId.put(pidorId, currentChainNum);
      }
      currentChainUserId = OptionalInt.of(dailyPidor.getPlayerTgId());
      currentChainNum = 1;
    }

    int pidorId = currentChainUserId.getAsInt();
    int topChain = topChainsByPidorId.getOrDefault(pidorId, 0);
    if (currentChainNum > topChain) {
      topChainsByPidorId.put(pidorId, currentChainNum);
    }

    Optional<Map.Entry<Integer, Integer>> topEntry =
        topChainsByPidorId.entrySet().stream().max(Map.Entry.comparingByValue());

    if (!topEntry.isPresent()) {
      return Optional.empty();
    }

    Optional<Pidor> pidor = pidorService.getPidor(chatId, topEntry.get().getKey());
    return pidor.map(
        value ->
            new TextBuilder(
                new SimpleText("Был пидором наибольшее число дней подряд:"),
                new NewLineText(),
                new NewLineText(),
                new ParametizedText(
                    "{0} - {1} дней подряд",
                    new FullNamePidorText(value),
                    new SimpleText(String.valueOf(topEntry.get().getValue())))));
  }
}
