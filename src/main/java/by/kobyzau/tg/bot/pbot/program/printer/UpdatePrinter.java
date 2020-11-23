package by.kobyzau.tg.bot.pbot.program.printer;

import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdatePrinter {

  private final Update update;
  private final int level;

  public UpdatePrinter(Update update) {
    this(update, 0);
  }

  public UpdatePrinter(Update update, int level) {
    this.update = update;
    this.level = level;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Message message = update.getMessage();
    Integer updateId = update.getUpdateId();
    final int newLevel = level + 2;
    sb.append(StringUtil.repeat("\t", level)).append("<i>Update:</i>\n");
    if (updateId != null) {
      sb.append(StringUtil.repeat("\t", newLevel)).append("<i>updateId:</i> ");
      sb.append(updateId);
      sb.append("\n");
    }
    if (message != null) {
      sb.append(new MessagePrinter(message, newLevel + 2));
    }
    return sb.toString();
  }
}
