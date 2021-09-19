package by.kobyzau.tg.bot.pbot.util;

import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberRestricted;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TGUtil {
  public static String getUsername(User user) {
    return escapeInput(StringUtil.isBlank(user.getUserName(), "").trim());
  }

  public static String getFullName(User user) {
    return escapeInput(
        (StringUtil.isBlank(user.getFirstName(), "")
                + " "
                + StringUtil.isBlank(user.getLastName(), ""))
            .trim());
  }

  public static String escapeInput(String s) {
    return s.replaceAll("\u2713", "")
        .replaceAll("\\u2713", "")
        .replaceAll("\u1160", "")
        .replaceAll("\\u1160", "")
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

  public static boolean canPinMessage(ChatMember chatMember) {
    String status = chatMember.getStatus();
    switch (status) {
      case "creator":
        return true;
      case "administrator":
        ChatMemberAdministrator chatMemberAdministrator = (ChatMemberAdministrator) chatMember;
        return chatMemberAdministrator.getCanPinMessages();
      case "restricted":
        ChatMemberRestricted chatMemberRestricted = (ChatMemberRestricted) chatMember;
        return chatMemberRestricted.getCanPinMessages();
      default:
        return false;
    }
  }

  public static boolean canDeleteMessage(ChatMember chatMember) {
    String status = chatMember.getStatus();
    switch (status) {
      case "creator":
        return true;
      case "administrator":
        ChatMemberAdministrator chatMemberAdministrator = (ChatMemberAdministrator) chatMember;
        return chatMemberAdministrator.getCanDeleteMessages();
      default:
        return false;
    }
  }

  public static boolean canSendOtherMessages(ChatMember chatMember) {
    String status = chatMember.getStatus();
    switch (status) {
      case "creator":
      case "administrator":
      case "member":
        return true;
      case "restricted":
        ChatMemberRestricted chatMemberRestricted = (ChatMemberRestricted) chatMember;
        return chatMemberRestricted.getCanSendOtherMessages();
      default:
        return false;
    }
  }
}
