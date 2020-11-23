package by.kobyzau.tg.bot.pbot.program.text;

import java.util.Objects;

public class SpaceText implements Text {

  @Override
  public String text() {
    return " ";
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
