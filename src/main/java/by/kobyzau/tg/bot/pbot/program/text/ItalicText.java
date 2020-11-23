package by.kobyzau.tg.bot.pbot.program.text;

import java.util.Objects;

public class ItalicText implements Text {

  private final Text text;

  public ItalicText(Text text) {
    this.text = new ParametizedText("<i>{0}</i>", text);
  }

  public ItalicText(String text) {
    this(new SimpleText(text));
  }

  @Override
  public String text() {
    return text.text();
  }

  @Override
  public String toString() {
    return text();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof Text) {
      return text().equals(((Text) o).text());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text());
  }
}
