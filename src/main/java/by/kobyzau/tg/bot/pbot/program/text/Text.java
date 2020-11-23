package by.kobyzau.tg.bot.pbot.program.text;

import by.kobyzau.tg.bot.pbot.util.StringUtil;

public interface Text {

  String text();

  default boolean isEmpty() {
    return StringUtil.isBlank(text());
  }
}
