package by.kobyzau.tg.bot.pbot.handlers.command.handler.achievement;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TopPidorAchievement implements Achievement {

  private final PidorService pidorService;

  private final DailyPidorRepository dailyPidorRepository;

  @Autowired
  public TopPidorAchievement(PidorService pidorService, DailyPidorRepository dailyPidorRepository) {
    this.pidorService = pidorService;
    this.dailyPidorRepository = dailyPidorRepository;
  }

  @Override
  public Optional<Text> getAchievementInfo(long chatId) {
    List<DailyPidor> dailyPidors = dailyPidorRepository.getByChat(chatId);
    if (CollectionUtil.isEmpty(dailyPidors)) {
      return Optional.empty();
    }
    int numDays = dailyPidors.size();

    Map<Long, Integer> numWinsById = new HashMap<>();
    for (DailyPidor dailyPidor : dailyPidors) {
      int numWins = numWinsById.getOrDefault(dailyPidor.getPlayerTgId(), 0);
      numWinsById.put(dailyPidor.getPlayerTgId(), numWins + 1);
    }
    int maxWinNum = numWinsById.values().stream().max(Integer::compareTo).orElse(0);

    if (maxWinNum == 0) {
      return Optional.empty();
    }
    List<Pidor> pidors =
        numWinsById.entrySet().stream()
            .filter(e -> e.getValue() == maxWinNum)
            .map(Map.Entry::getKey)
            .map(id -> pidorService.getPidor(chatId, id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

    if (pidors.isEmpty()) {
      return Optional.empty();
    }
    TextBuilder textBuilder = new TextBuilder(new SimpleText("???????? ?????????? ?????? ?????????????? ??????:"));
    textBuilder.append(new NewLineText());
    textBuilder.append(new NewLineText());
    for (Pidor pidor : pidors) {
      int num = numWinsById.getOrDefault(pidor.getTgId(), 0);
      textBuilder
          .append(
              new ParametizedText(
                  "{0} - ?????? ?????????????? {1} ??????(??) ({2}%)",
                  new FullNamePidorText(pidor), new IntText(num), new IntText(100 * num / numDays)))
          .append(new NewLineText());
    }

    return Optional.of(textBuilder);
  }
}
