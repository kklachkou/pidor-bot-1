package by.kobyzau.tg.bot.pbot.program.text;

import java.time.LocalDateTime;

public class DateTimeText implements Text {

  private final LocalDateTime localDateTime;

  public DateTimeText(LocalDateTime localDateTime) {
    this.localDateTime = localDateTime;
  }

  @Override
  public String text() {
    return localDateTime.toString();
  }

  @Override
  public String toString() {
    return text();
  }
}
