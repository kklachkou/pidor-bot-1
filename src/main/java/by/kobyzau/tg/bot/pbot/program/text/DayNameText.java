package by.kobyzau.tg.bot.pbot.program.text;

import java.util.Objects;

public class DayNameText implements Text {

  private final int day;

  public DayNameText(int day) {
    this.day = day;
  }

  @Override
  public String text() {
    switch (day) {
      case 1:
          return "Понедельник";
      case 2:
          return "Вторник";
      case 3:
          return "Среда";
      case 4:
          return "Четверг";
      case 5:
          return "Пятница";
      case 6:
          return "Суббота";
      case 7:
          return "Воскресенье";
    }
    throw new IllegalArgumentException("Invalid day:  " + day);
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
