package by.kobyzau.tg.bot.pbot.program.text;

public class IntText implements Text {

  private final int value;

  public IntText(int value) {
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
}
