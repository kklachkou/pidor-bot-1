package by.kobyzau.tg.bot.pbot.program.text;

import java.time.LocalDateTime;
import java.util.Objects;

public class TimeLeftText implements Text {

  private static final String PATTERN = "%sч %sмин";
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;

  public TimeLeftText(LocalDateTime startTime, LocalDateTime endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  @Override
  public String text() {
    if (startTime == null || endTime == null) {
      return "";
    }
    if (startTime.isAfter(endTime)) {
      return String.format(PATTERN, 0, 0);
    }
    int totalMinutes =
        endTime.getHour() * 60
            + endTime.getMinute()
            - startTime.getHour() * 60
            - startTime.getMinute();
    int hour = totalMinutes / 60;
    int min = totalMinutes - hour * 60;
    return String.format(PATTERN, hour, min);
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
