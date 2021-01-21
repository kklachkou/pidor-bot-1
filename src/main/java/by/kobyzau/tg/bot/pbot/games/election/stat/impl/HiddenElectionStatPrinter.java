package by.kobyzau.tg.bot.pbot.games.election.stat.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.games.election.stat.ElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component("hiddenElectionStatPrinter")
public class HiddenElectionStatPrinter implements ElectionStatPrinter {

  @Autowired private PidorService pidorService;

  @Autowired private ElectionService electionService;

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void printInfo(long chatId) {
    botActionCollector.typing(chatId);
    LocalDate now = DateUtil.now();
    TextBuilder textBuilder =
        new TextBuilder(
            new RandomText(
                "Информация закрытого голосования:", "Информация по закрытому голосованию:"));
    textBuilder.append(new NewLineText());
    textBuilder.append(new NewLineText());
    List<Pidor> pidors = pidorService.getByChat(chatId);
    int totalVotes = electionService.getNumVotes(chatId, now);
    int numToVote = electionService.getNumToVote(chatId);
    textBuilder
        .append(new ParametizedText("Явка {0}%.", new IntText(100 * totalVotes / pidors.size())))
        .append(
            new ParametizedText(
                " Для оглашения результата необходимо ещё {0} голосов",
                new IntText(numToVote - totalVotes)))
        .append(new NewLineText());

    botActionCollector.text(chatId, textBuilder);
  }
}
