package by.kobyzau.tg.bot.pbot.program.text;

import java.util.Objects;

public class SimpleText implements Text {

  private final String text;

  public SimpleText(String text) {
    this.text = text;
  }

  @Override
  public String text() {
    return text;
  }

  @Override
  public String toString() {
    return text();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof Text) {
      return ((Text) o).text().equals(text());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text());
  }
}
