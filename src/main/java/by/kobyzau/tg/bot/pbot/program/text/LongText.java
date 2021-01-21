package by.kobyzau.tg.bot.pbot.program.text;

import java.util.Objects;

public class LongText implements Text {

  private final long value;

  public LongText(long value) {
    this.value = value;
  }

  @Override
  public String text() {
    return String.valueOf(value);
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
