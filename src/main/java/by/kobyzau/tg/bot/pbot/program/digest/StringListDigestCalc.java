package by.kobyzau.tg.bot.pbot.program.digest;

import java.util.List;
import java.util.stream.Collectors;

import by.kobyzau.tg.bot.pbot.util.CollectionUtil;

public class StringListDigestCalc implements DigestCalc<List<String>> {

  @Override
  public String getDigest(List<String> list) {
    if (CollectionUtil.isEmpty(list)) {
      return "empty";
    }
    StringDigestCalc stringDigestCalc = new StringDigestCalc();
    return list.stream().map(stringDigestCalc::getDigest).collect(Collectors.joining("|"));
  }
}
