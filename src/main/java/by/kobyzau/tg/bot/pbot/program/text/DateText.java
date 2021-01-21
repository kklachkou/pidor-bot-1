package by.kobyzau.tg.bot.pbot.program.text;

import java.time.LocalDate;
import java.util.Objects;

public class DateText implements Text {

  private final LocalDate localDate;

  public DateText(LocalDate localDate) {
    this.localDate = localDate;
  }

  @Override
  public String text() {
    return localDate.getDayOfMonth() + "." + localDate.getMonthValue() + "." + localDate.getYear();
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
