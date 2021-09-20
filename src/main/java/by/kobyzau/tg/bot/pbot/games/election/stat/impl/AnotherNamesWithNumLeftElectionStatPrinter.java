package by.kobyzau.tg.bot.pbot.games.election.stat.impl;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.games.election.ElectionPidorComparator;
import by.kobyzau.tg.bot.pbot.games.election.stat.ElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component("anotherNamesWithNumLeftElectionStatPrinter")
public class AnotherNamesWithNumLeftElectionStatPrinter implements ElectionStatPrinter {

  @Autowired private PidorService pidorService;

  @Autowired private ElectionService electionService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private UserArtifactService userArtifactService;

  private static final List<String> NAMES =
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
          "Всевидящий видящий",
          "Просто Тод");

  @Override
  public void printInfo(long chatId) {
    botActionCollector.typing(chatId);
    List<String> namesByDay = CollectionUtil.getRandomListByDay(NAMES, DateUtil.now());
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
    int numToVote = electionService.getNumToVote(chatId);
    textBuilder
        .append(new ParametizedText("Явка {0}%.", new IntText(100 * totalVotes / pidors.size())))
        .append(
            new ParametizedText(
                " Для оглашения результата необходимо ещё {0} голосов",
                new IntText(numToVote - totalVotes)))
        .append(new NewLineText());
    boolean hasWithZero = false;
    for (Pidor pidor : pidors) {
      boolean hasMagnet =
          userArtifactService
              .getUserArtifact(chatId, pidor.getTgId(), ArtifactType.PIDOR_MAGNET)
              .isPresent();
      if (hasMagnet) {
        totalVotes = totalVotes + 3;
      }
    }
    for (int index = 0; index < pidors.size(); index++) {
      Pidor pidor = pidors.get(index);
      boolean hasMagnet =
          userArtifactService
              .getUserArtifact(chatId, pidor.getTgId(), ArtifactType.PIDOR_MAGNET)
              .isPresent();
      int numVotes = electionService.getNumVotes(chatId, now, pidor.getTgId());
      if (numVotes == 0 && !hasMagnet) {
        hasWithZero = true;
        continue;
      }
      textBuilder.append(new NewLineText());
      double chance =
          100 * (numVotes + (hasMagnet ? 4 : 1)) / ((double) totalVotes + pidors.size());
      textBuilder
          .append(new SimpleText(CollectionUtil.getItem(namesByDay, index)))
          .append(
              hasMagnet
                  ? new SimpleText(" " + ArtifactType.PIDOR_MAGNET.getEmoji())
                  : new EmptyText())
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
