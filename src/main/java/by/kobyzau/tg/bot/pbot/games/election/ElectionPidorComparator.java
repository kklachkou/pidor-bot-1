package by.kobyzau.tg.bot.pbot.games.election;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@RequiredArgsConstructor
public class ElectionPidorComparator implements Comparator<Pidor> {

  private final ElectionService electionService;

  @Override
  public int compare(Pidor p1, Pidor p2) {
    int numP1 =
        electionService.getNumVotes(p1.getChatId(), DateUtil.now(), p1.getTgId())
            + electionService.getNumSuperVotes(p1.getChatId(), DateUtil.now(), p1.getTgId());
    int numP2 =
        electionService.getNumVotes(p2.getChatId(), DateUtil.now(), p2.getTgId())
            + electionService.getNumSuperVotes(p2.getChatId(), DateUtil.now(), p2.getTgId());
    int compareVotes = Integer.compare(numP2, numP1);
    if (compareVotes != 0) {
      return compareVotes;
    }
    return Long.compare(p1.getId(), p2.getId());
  }
}
