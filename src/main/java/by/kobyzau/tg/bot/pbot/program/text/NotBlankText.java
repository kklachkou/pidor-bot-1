package by.kobyzau.tg.bot.pbot.program.text;

import java.util.Optional;

public class NotBlankText implements Text {

  private final Text text;
  private final Text def;

  public NotBlankText(Text text, Text def) {
    this.text = text;
    this.def = def;
  }

  public NotBlankText(Text text) {
    this(text, new SimpleText(""));
  }

  public NotBlankText(String text) {
    this(new SimpleText(text));
  }

  public NotBlankText(String text, String def) {
    this(new SimpleText(text), new SimpleText(def));
  }

  @Override
  public String text() {
    return Optional.ofNullable(text).map(Text::text).filter(this::isNotBlank).orElse(def.text());
  }

  private boolean isNotBlank(String s) {
    return !s.trim().isEmpty();
  }

  @Override
  public String toString() {
    return text();
  }
}
