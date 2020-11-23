package by.kobyzau.tg.bot.pbot.program.text;

import java.util.Optional;

public class TrimmedText implements Text {

  private final Text text;

  public TrimmedText(Text text) {
    this.text = text;
  }

  public TrimmedText(String text) {
    this(new SimpleText(text));
  }

  @Override
  public String text() {
    return new NotBlankText(text).text().trim();
  }

  @Override
  public String toString() {
    return text();
  }
}
