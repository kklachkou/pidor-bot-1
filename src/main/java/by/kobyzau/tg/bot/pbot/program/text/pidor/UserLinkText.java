package by.kobyzau.tg.bot.pbot.program.text.pidor;

import by.kobyzau.tg.bot.pbot.program.text.Text;

public class UserLinkText implements Text {

  private final int userId;
  private final Text text;

  public UserLinkText(int userId, Text text) {
    this.userId = userId;
    this.text = text;
  }

  @Override
  public String text() {
    return "<a href=\"tg://user?id=" + userId + "\">" + text + "</a>";
  }

  @Override
  public String toString() {
    return text();
  }
}
