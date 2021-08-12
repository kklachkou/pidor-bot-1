package by.kobyzau.tg.bot.pbot.program.text;

import java.time.LocalDateTime;
import java.util.Objects;

public class TimeText implements Text {

  private final LocalDateTime localDateTime;

  public TimeText(LocalDateTime localDateTime) {
    this.localDateTime = localDateTime;
  }

  @Override
  public String text() {
    if (localDateTime == null) {
      return "";
    }
    return localDateTime.getHour() + ":" + localDateTime.getMinute();
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
