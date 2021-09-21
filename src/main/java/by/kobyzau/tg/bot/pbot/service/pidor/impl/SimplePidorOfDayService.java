package by.kobyzau.tg.bot.pbot.service.pidor.impl;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.pidor.PidorOfDayService;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimplePidorOfDayService implements PidorOfDayService {

  @Autowired private PidorService pidorService;
  @Autowired private UserArtifactService userArtifactService;

  @Override
  public Pidor findPidorOfDay(long chatId) {
    List<UserArtifact> userArtifacts = userArtifactService.getUserArtifacts(chatId);
    List<Pidor> pidors = pidorService.getByChat(chatId);
    List<Pidor> pidorsForSearch = new ArrayList<>();
    for (Pidor pidor : pidors) {
      boolean hasAntiPidor =
          userArtifacts.stream()
              .anyMatch(
                  a ->
                      a.getUserId() == pidor.getTgId()
                          && a.getArtifactType() == ArtifactType.ANTI_PIDOR);
      if (!hasAntiPidor) {
        pidorsForSearch.add(pidor);
      }
    }
    if (pidorsForSearch.isEmpty()) {
      pidorsForSearch.addAll(pidors);
    }
    if (CollectionUtil.isNotEmpty(userArtifacts)) {
      for (Pidor pidor : pidorsForSearch.stream().distinct().collect(Collectors.toList())) {
        if (userArtifacts.stream()
            .anyMatch(
                a ->
                    a.getUserId() == pidor.getTgId()
                        && a.getArtifactType() == ArtifactType.PIDOR_MAGNET)) {
          pidorsForSearch.add(pidor);
          pidorsForSearch.add(pidor);
          pidorsForSearch.add(pidor);
        }
      }
    }

    return CollectionUtil.getRandomValue(pidorsForSearch);
  }

  @Override
  public Type getType() {
    return Type.SIMPLE;
  }
}
