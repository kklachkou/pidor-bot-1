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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ElectionPidorOfDayService implements PidorOfDayService {

  @Autowired private PidorService pidorService;
  @Autowired private UserArtifactService userArtifactService;
  @Autowired private ElectionService electionService;

  @Override
  public Pidor findPidorOfDay(long chatId) {
    log.info("Finding election pidor in chat {}", chatId);
    LocalDate now = DateUtil.now();
    List<Pidor> pidors = pidorService.getByChat(chatId);
    List<Pidor> pidorsForSearch = new ArrayList<>(pidors);
    for (Pidor pidor : pidors) {
      for (int i = 0; i < electionService.getNumVotes(chatId, now, pidor.getTgId()); i++) {
        pidorsForSearch.add(pidor);
      }
    }
    List<UserArtifact> userArtifacts = userArtifactService.getUserArtifacts(chatId);
    if (CollectionUtil.isNotEmpty(userArtifacts)) {
      for (Pidor pidor : pidors) {
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
    return Type.ELECTION;
  }
}
