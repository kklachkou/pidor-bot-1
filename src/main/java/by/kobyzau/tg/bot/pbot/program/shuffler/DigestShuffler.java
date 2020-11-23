package by.kobyzau.tg.bot.pbot.program.shuffler;

import by.kobyzau.tg.bot.pbot.program.digest.DigestCalc;
import by.kobyzau.tg.bot.pbot.program.list.DigestedList;
import by.kobyzau.tg.bot.pbot.program.list.ListWrapper;
import by.kobyzau.tg.bot.pbot.program.list.NotDigestedList;
import by.kobyzau.tg.bot.pbot.program.list.ShuffledList;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;

import java.util.Collections;
import java.util.List;

public class DigestShuffler<T> implements Shuffler<T> {

  private final List<String> lastUsedDigests;
  private final DigestCalc<T> digestCalc;

  public DigestShuffler(List<String> lastUsedDigests, DigestCalc<T> digestCalc) {
    this.lastUsedDigests = lastUsedDigests;
    this.digestCalc = digestCalc;
  }

  @Override
  public List<T> apply(List<T> list) {

    if (CollectionUtil.isEmpty(list)) {
      return Collections.emptyList();
    }

    if (list.size() == 1) {
      return Collections.singletonList(list.get(0));
    }
    if (CollectionUtil.isEmpty(lastUsedDigests)) {
      return CollectionUtil.getRandomList(list);
    }
    return new ListWrapper<>(
            new ShuffledList<>(new NotDigestedList<>(list, digestCalc, lastUsedDigests)),
            new ShuffledList<>(new DigestedList<>(list, digestCalc, lastUsedDigests)))
        .getList();
  }
}
