package by.kobyzau.tg.bot.pbot.program.text;

public class SimpleMonth implements Text {

  private final String month;

  public SimpleMonth(int month) {
    this.month = getSimpleMonth(month);
  }

  @Override
  public String text() {
    return month;
  }

  @Override
  public String toString() {
    return text();
  }

  private String getSimpleMonth(int month) {
    switch (month) {
      case 1:
        return "Январь";
      case 2:
        return "Февраль";
      case 3:
        return "Март";
      case 4:
        return "Апрель";
      case 5:
        return "Май";
      case 6:
        return "Июнь";
      case 7:
        return "Июль";
      case 8:
        return "Август";
      case 9:
        return "Сентябрь";
      case 10:
        return "Октябрь";
      case 11:
        return "Ноябрь";
      case 12:
        return "Декабрь";
      default:
        throw new IllegalArgumentException("Invalid month: " + month);
    }
  }
}
