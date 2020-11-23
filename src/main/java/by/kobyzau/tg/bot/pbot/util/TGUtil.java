package by.kobyzau.tg.bot.pbot.util;

import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TGUtil {
  public static String getUsername(User user) {
    return StringUtil.isBlank(user.getUserName(), "").trim();
  }

  public static String getFullName(User user) {
    return (StringUtil.isBlank(user.getFirstName(), "")
            + " "
            + StringUtil.isBlank(user.getLastName(), ""))
        .trim();
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
