package by.kobyzau.tg.bot.pbot.games.election;

import by.kobyzau.tg.bot.pbot.checker.BotActionAbstractTest;
import by.kobyzau.tg.bot.pbot.checker.BotTypeBotActionChecker;
import by.kobyzau.tg.bot.pbot.checker.ContainsTextBotActionChecker;
import by.kobyzau.tg.bot.pbot.games.election.stat.ElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.games.election.stat.impl.HiddenElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.ChatActionBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class HiddenElectionStatPrinterTest extends BotActionAbstractTest {

  @InjectMocks private ElectionStatPrinter printer = new HiddenElectionStatPrinter();

  @Mock private PidorService pidorService;

  @Mock private ElectionService electionService;

  private final long chatId = 123;

  @Test
  public void printInfo_info() {
    // given
    List<Pair<Integer, Integer>> pidorIdsAndVotes =
        Arrays.asList(new Pair<>(1, 1), new Pair<>(2, 4), new Pair<>(3, 2));
    LocalDate now = DateUtil.now();
    List<Pidor> pidors = new ArrayList<>();
    int totalVotes = 0;
    for (Pair<Integer, Integer> pidorIdsAndVote : pidorIdsAndVotes) {
      pidors.add(getPidor(pidorIdsAndVote.getLeft()));
      doReturn(pidorIdsAndVote.getRight())
          .when(electionService)
          .getNumVotes(chatId, now, pidorIdsAndVote.getLeft());
      totalVotes += pidorIdsAndVote.getRight();
    }
    doReturn(pidors).when(pidorService).getByChat(chatId);
    doReturn(totalVotes).when(electionService).getNumVotes(chatId, now);
    doReturn(pidors.size()).when(electionService).getNumToVote(chatId);

    // when
    printer.printInfo(chatId);

    // then

    checkActions(
        new BotTypeBotActionChecker(ChatActionBotAction.class),
        new ContainsTextBotActionChecker(
            "Информация закрытого голосования:", "Информация по закрытому голосованию:"));
  }

  private Pidor getPidor(int id) {
    return new Pidor(id, chatId, "Pidor" + id);
  }
}
