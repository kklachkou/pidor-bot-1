package by.kobyzau.tg.bot.pbot.program.text;

import java.util.Objects;

public class StrikethroughText implements Text {

  private final Text inner;

  public StrikethroughText(Text inner) {
    this.inner = inner;
  }

  @Override
  public String text() {
    return "<s>" + inner.text() + "</s>";
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
