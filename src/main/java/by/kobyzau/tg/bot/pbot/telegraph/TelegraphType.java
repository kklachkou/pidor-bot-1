package by.kobyzau.tg.bot.pbot.telegraph;

import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TelegraphType {
  TEMP, STATISTIC;

  public String getLinkedId(Object... param) {
    StringBuilder sb = new StringBuilder(name());
    if (param != null) {
      for (Object p : param) {
        sb.append(":").append(p.toString());
      }
    }
    return sb.toString();
  }

  public static Optional<TelegraphType> parseByLinkedId(String linkedId) {
    if (StringUtil.isBlank(linkedId)) {
      return Optional.empty();
    }
    for (TelegraphType telegraphType : values()) {
      Pattern pattern = telegraphType.getLinkedIdPattern();
      Matcher matcher = pattern.matcher(linkedId);
      if (matcher.matches()) {
        return Optional.of(telegraphType);
      }
    }
    return Optional.empty();
  }

  private Pattern getLinkedIdPattern() {
    return Pattern.compile("^" + name() + "(:.+)*");
  }
}
