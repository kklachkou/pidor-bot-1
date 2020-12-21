package by.kobyzau.tg.bot.pbot.util;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNameLinkedPidorText;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElectionStatPrinter {

  @Autowired private PidorService pidorService;

  @Autowired private ElectionService electionService;

  @Autowired private BotActionCollector botActionCollector;

  public void printInfo(long chatId, boolean displayCount) {
    botActionCollector.typing(chatId);
    LocalDate now = DateUtil.now();
    TextBuilder textBuilder =
        new TextBuilder(
            new RandomText(
                "Информация голосования:",
                "Информация по голосованию:",
                "Предворительная информация голосования:"));
    textBuilder.append(new NewLineText());
    textBuilder.append(new NewLineText());
    Comparator<Pidor> comparator =
        Comparator.comparingInt(p -> electionService.getNumVotes(chatId, now, p.getTgId()));
    List<Pidor> pidors =
        pidorService.getByChat(chatId).stream()
            .sorted(comparator.reversed())
            .collect(Collectors.toList());
    int totalVotes = electionService.getNumVotes(chatId, now);
    int numToVote = electionService.getNumToVote(chatId);
    textBuilder
        .append(new ParametizedText("Явка {0}%.", new IntText(100 * totalVotes / numToVote)))
        .append(
            displayCount
                ? new ParametizedText(
                    " Для оглашения результата необходимо ещё {0} голосов",
                    new IntText(numToVote - totalVotes))
                : new EmptyText())
        .append(new NewLineText());
    for (Pidor pidor : pidors) {
      textBuilder.append(new NewLineText());
      int numVotes = electionService.getNumVotes(chatId, now, pidor.getTgId());
      double chance = (100 * (numVotes + 1) / ((double) totalVotes + pidors.size()));
      textBuilder
          .append(new ShortNameLinkedPidorText(pidor))
          .append(
              new ParametizedText(
                  " - {0} голосов. Шанс стать пидором - {1}%",
                  new IntText(numVotes), new DoubleText(chance)))
          .append(new NewLineText());
    }
    botActionCollector.text(chatId, textBuilder);
  }
}
