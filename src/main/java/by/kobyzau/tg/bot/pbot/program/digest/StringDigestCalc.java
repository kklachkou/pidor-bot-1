package by.kobyzau.tg.bot.pbot.program.digest;

public class StringDigestCalc implements DigestCalc<String> {
  @Override
  public String getDigest(String obj) {
    if (obj == null) {
      return "null";
    }
    return "hash" + obj.hashCode();
  }
}
