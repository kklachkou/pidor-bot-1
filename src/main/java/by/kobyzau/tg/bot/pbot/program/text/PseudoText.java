package by.kobyzau.tg.bot.pbot.program.text;

import by.kobyzau.tg.bot.pbot.util.PseudoLocalizerUtil;

public class PseudoText implements Text {

  private final Text text;

  public PseudoText(Text text) {
    this.text = text;
  }

  public PseudoText(String text) {
    this(new SimpleText(text));
  }

  @Override
  public String text() {
    return PseudoLocalizerUtil.transform(text.text());
  }

  @Override
  public String toString() {
    return text();
  }
}
