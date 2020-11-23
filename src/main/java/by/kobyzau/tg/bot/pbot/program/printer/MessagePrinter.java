package by.kobyzau.tg.bot.pbot.program.printer;

import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Dice;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class MessagePrinter {

  private final Message message;
  private final int level;

  public MessagePrinter(Message message) {
    this(message, 0);
  }

  public MessagePrinter(Message message, int level) {
    this.message = message;
    this.level = level;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    User from = message.getFrom();
    Dice dice = message.getDice();
    Chat chat = message.getChat();
    Integer messageId = message.getMessageId();
    String text = TGUtil.escapeHTML(message.getText());
    final int newLevel = level + 2;
    sb.append(StringUtil.repeat("\t", level)).append("<i>Message:</i>\n");
    if (messageId != null) {
      sb.append(StringUtil.repeat("\t", newLevel)).append("<i>messageId:</i> ");
      sb.append(messageId);
      sb.append("\n");
    }
    if (StringUtil.isNotBlank(text)) {
      sb.append(StringUtil.repeat("\t", newLevel)).append("<i>text:</i> ");
      sb.append(text);
      sb.append("\n");
    }
    if (chat != null) {
      sb.append(new ChatPrinter(chat, newLevel + 2));
    }
    if (from != null) {
      sb.append(new UserPrinter("FromUser", from, newLevel + 2));
    }
    if (dice != null) {
      sb.append(StringUtil.repeat("\t", newLevel)).append("<i>dice: </i> ");
      sb.append(dice.getEmoji()).append(": ").append(dice.getValue());
      sb.append("\n");
    }
    return sb.toString();
  }
}
