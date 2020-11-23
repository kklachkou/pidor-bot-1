package by.kobyzau.tg.bot.pbot.program.text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

public class DoubleText implements Text {

  private final double value;

  public DoubleText(double value) {
    this.value = value;
  }

  @Override
  public String text() {
    NumberFormat formatter = new DecimalFormat("#0.00");
    return formatter.format(value);
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
