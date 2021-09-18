package by.kobyzau.tg.bot.pbot.service.pidor.impl;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameResult;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorDice;
import by.kobyzau.tg.bot.pbot.service.DiceService;
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
import java.util.stream.Collectors;

@Slf4j
@Service
public class DicePidorOfDayService implements PidorOfDayService {

  @Autowired private PidorService pidorService;
  @Autowired private UserArtifactService userArtifactService;
  @Autowired private DiceService diceService;

  @Override
  public Pidor findPidorOfDay(long chatId) {
    log.info("Finding dice pidor in chat {}", chatId);
    LocalDate now = DateUtil.now();
    EmojiGame game =
        diceService
            .getGame(chatId, now)
            .orElseThrow(
                () -> new IllegalStateException("Cannot find dice game for chat " + chatId));
    List<Pidor> pidors = pidorService.getByChat(chatId);
    List<UserArtifact> userArtifacts = userArtifactService.getUserArtifacts(chatId, now);
    List<Pidor> pidorsForSearch = new ArrayList<>();
    for (Pidor pidor : pidors) {
      int userDice = getUserDice(pidor);
      EmojiGameResult gameResult = game.getResult(chatId, userDice);
      switch (gameResult) {
        case LOSE:
        case DRAW:
          pidorsForSearch.add(pidor);
          break;
        case NONE:
          pidorsForSearch.add(pidor);
          pidorsForSearch.add(pidor);
          break;
      }
    }
    if (CollectionUtil.isNotEmpty(userArtifacts)) {
      for (Pidor pidor : pidorsForSearch.stream().distinct().collect(Collectors.toList())) {
        if (userArtifacts.stream()
            .anyMatch(
                a ->
                    a.getUserId() == pidor.getTgId()
                        && a.getArtifactType() == ArtifactType.PIDOR_MAGNET)) {
          pidorsForSearch.add(pidor);
        }
      }
    }
    return CollectionUtil.getRandomValue(pidorsForSearch);
  }

  private int getUserDice(Pidor pidor) {
    return diceService
        .getUserDice(pidor.getChatId(), pidor.getTgId(), DateUtil.now())
        .map(PidorDice::getValue)
        .orElse(0);
  }

  @Override
  public Type getType() {
    return Type.DICE;
  }
}
