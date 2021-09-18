package by.kobyzau.tg.bot.pbot.games.election.stat.impl;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.games.election.ElectionPidorComparator;
import by.kobyzau.tg.bot.pbot.games.election.stat.ElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNameLinkedPidorText;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component("fullElectionStatPrinter")
public class FullElectionStatPrinter implements ElectionStatPrinter {

  @Autowired private PidorService pidorService;

  @Autowired private ElectionService electionService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private UserArtifactService userArtifactService;

  @Override
  public void printInfo(long chatId) {
    botActionCollector.typing(chatId);
    LocalDate now = DateUtil.now();
    TextBuilder textBuilder =
        new TextBuilder(
            new RandomText(
                "Информация голосования:",
                "Информация по голосованию:",
                "Предварительная информация голосования:"));
    textBuilder.append(new NewLineText());
    textBuilder.append(new NewLineText());
    List<Pidor> pidors =
        pidorService.getByChat(chatId).stream()
            .sorted(new ElectionPidorComparator(electionService))
            .collect(Collectors.toList());
    int totalVotes = electionService.getNumVotes(chatId, now);
    textBuilder
        .append(new ParametizedText("Явка {0}%.", new IntText(100 * totalVotes / pidors.size())))
        .append(new NewLineText());
    boolean hasWithZero = false;
    for (Pidor pidor : pidors) {
      boolean hasMagnet =
          userArtifactService
              .getUserArtifact(chatId, pidor.getTgId(), ArtifactType.PIDOR_MAGNET, now)
              .isPresent();
      if (hasMagnet) {
        totalVotes++;
      }
    }
    for (Pidor pidor : pidors) {
      boolean hasMagnet =
          userArtifactService
              .getUserArtifact(chatId, pidor.getTgId(), ArtifactType.PIDOR_MAGNET, now)
              .isPresent();
      int numVotes = electionService.getNumVotes(chatId, now, pidor.getTgId());
      if (numVotes == 0 && !hasMagnet) {
        hasWithZero = true;
        continue;
      }
      textBuilder.append(new NewLineText());
      double chance =
          100 * (numVotes + (hasMagnet ? 2 : 1)) / ((double) totalVotes + pidors.size());
      textBuilder
          .append(new ShortNameLinkedPidorText(pidor))
          .append(
              new ParametizedText(
                  " - {0} голосов. Шанс стать пидором - {1}%",
                  new IntText(numVotes), new DoubleText(chance)))
          .append(new NewLineText());
    }
    if (hasWithZero) {
      textBuilder.append(new NewLineText());
      double zeroChance = 100 / ((double) totalVotes + pidors.size());
      textBuilder
          .append(new SimpleText("У всех остальных"))
          .append(
              new ParametizedText(
                  " - 0 голосов. Шанс стать пидором - {0}%", new DoubleText(zeroChance)))
          .append(new NewLineText());
    }
    botActionCollector.text(chatId, textBuilder);
  }
}
