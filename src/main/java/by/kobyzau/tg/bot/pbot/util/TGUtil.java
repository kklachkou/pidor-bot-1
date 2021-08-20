package by.kobyzau.tg.bot.pbot.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

public class TGUtil {
  public static String getUsername(User user) {
    return StringUtil.isBlank(user.getUserName(), "")
        .trim()
        .replaceAll("\uD83C\uDDE7\uD83C\uDDFE", "\uD83D\uDCA9");
  }

  public static String getFullName(User user) {
    return (StringUtil.isBlank(user.getFirstName(), "")
            + " "
            + StringUtil.isBlank(user.getLastName(), ""))
        .trim()
        .replaceAll("\uD83C\uDDE7\uD83C\uDDFE", "\uD83D\uDCA9");
  }

  public static boolean isChatMember(Optional<ChatMember> chatMember) {
    Set<String> badStatuses = new HashSet<>(Arrays.asList("left", "kicked"));
    return chatMember.filter(c -> !badStatuses.contains(c.getStatus())).isPresent();
  }

  public static String escapeHTML(String s) {
    if (StringUtil.isBlank(s)) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      switch (c) {
        case '<':
          sb.append("&lt;");
          break;
        case '>':
          sb.append("&gt;");
          break;
        case '&':
          sb.append("&amp;");
          break;
        default:
          sb.append(c);
      }
    }
    return sb.toString().replaceAll("m̎͐", "m");
  }
}
