package by.kobyzau.tg.bot.pbot.program.text;

public class PastMonth implements Text {

  private final String month;

  public PastMonth(int month) {
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
        return "Января";
      case 2:
        return "Февраля";
      case 3:
        return "Марта";
      case 4:
        return "Апреля";
      case 5:
        return "Мая";
      case 6:
        return "Июня";
      case 7:
        return "Июля";
      case 8:
        return "Августа";
      case 9:
        return "Сентября";
      case 10:
        return "Октября";
      case 11:
        return "Норября";
      case 12:
        return "Декабря";
      default:
        throw new IllegalArgumentException("Invalid month: " + month);
    }
  }
}
