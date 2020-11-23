package by.kobyzau.tg.bot.pbot.program.list;

import by.kobyzau.tg.bot.pbot.program.digest.DigestCalc;

import java.util.List;
import java.util.stream.Collectors;

public class NotDigestedList<T> implements ListProvider<T> {

  private final List<T> list;
  private final DigestCalc<T> digestCalc;
  private final List<String> digests;

  public NotDigestedList(List<T> list, DigestCalc<T> digestCalc, List<String> digests) {
    this.list = list;
    this.digestCalc = digestCalc;
    this.digests = digests;
  }

  @Override
  public List<T> getList() {
    return list.stream()
        .filter(i -> !digests.contains(digestCalc.getDigest(i)))
        .collect(Collectors.toList());
  }
}
