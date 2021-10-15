package by.kobyzau.tg.bot.pbot.service.pidor.impl;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.pidor.PidorOfDayService;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElectionPidorOfDayService implements PidorOfDayService {

  @Autowired private PidorService pidorService;
  @Autowired private UserArtifactService userArtifactService;
  @Autowired private ElectionService electionService;

  @Override
  public Pidor findPidorOfDay(long chatId) {
    LocalDate now = DateUtil.now();
    List<Pidor> pidors = pidorService.getByChat(chatId);
    List<Pidor> pidorsForSearch = new ArrayList<>();
    List<UserArtifact> userArtifacts = userArtifactService.getUserArtifacts(chatId);
    for (Pidor pidor : pidors) {
      boolean hasAntiPidor =
          userArtifacts.stream()
              .anyMatch(
                  a ->
                      a.getUserId() == pidor.getTgId()
                          && a.getArtifactType() == ArtifactType.ANTI_PIDOR);
      if (hasAntiPidor) {
        continue;
      }
      pidorsForSearch.add(pidor);
      for (int i = 0; i < electionService.getNumVotes(chatId, now, pidor.getTgId()); i++) {
        pidorsForSearch.add(pidor);
      }
      for (int i = 0; i < electionService.getNumSuperVotes(chatId, now, pidor.getTgId()); i++) {
        pidorsForSearch.add(pidor);
      }
    }
    if (pidorsForSearch.isEmpty()) {
      pidorsForSearch.addAll(pidors);
    }

    if (CollectionUtil.isNotEmpty(userArtifacts)) {
      for (Pidor pidor : pidorsForSearch.stream().distinct().collect(Collectors.toList())) {
        boolean hasAntiPidor =
            userArtifacts.stream()
                .anyMatch(
                    a ->
                        a.getUserId() == pidor.getTgId()
                            && a.getArtifactType() == ArtifactType.ANTI_PIDOR);
        if (hasAntiPidor) {
          continue;
        }
        boolean hasMagnet =
            userArtifacts.stream()
                .anyMatch(
                    a ->
                        a.getUserId() == pidor.getTgId()
                            && a.getArtifactType() == ArtifactType.PIDOR_MAGNET);
        if (hasMagnet) {
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
    return Type.ELECTION;
  }
}
