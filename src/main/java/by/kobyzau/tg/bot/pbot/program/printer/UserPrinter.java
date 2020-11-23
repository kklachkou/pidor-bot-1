package by.kobyzau.tg.bot.pbot.program.printer;

import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.telegram.telegrambots.meta.api.objects.User;

public class UserPrinter {

  private final String title;
  private final User user;
  private final int level;

  public UserPrinter(User user) {
    this("User", user, 0);
  }

  public UserPrinter(String title, User user, int level) {
    this.user = user;
    this.title = title;
    this.level = level;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Integer id = user.getId();
    String firstName = TGUtil.escapeHTML(user.getFirstName());
    String userName = TGUtil.escapeHTML(user.getUserName());
    String languageCode = user.getLanguageCode();
    final int newLevel = level + 1;
    sb.append(StringUtil.repeat("\t", level)).append("<i>").append(title).append("</i>:\n");
    if (id != null) {
      sb.append(StringUtil.repeat("\t", newLevel)).append("<i>id:</i>: ");
      sb.append(id);
      sb.append("\n");
    }
    if (StringUtil.isNotBlank(firstName)) {
      sb.append(StringUtil.repeat("\t", newLevel)).append("<i>firstName:</i>: ");
      sb.append(firstName);
      sb.append("\n");
    }
    if (StringUtil.isNotBlank(userName)) {
      sb.append(StringUtil.repeat("\t", newLevel)).append("<i>userName:</i> ");
      sb.append(userName);
      sb.append("\n");
    }
    if (StringUtil.isNotBlank(languageCode)) {
      sb.append(StringUtil.repeat("\t", newLevel)).append("<i>languageCode:</i> ");
      sb.append(languageCode);
      sb.append("\n");
    }
    return sb.toString();
  }
}
