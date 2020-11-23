package by.kobyzau.tg.bot.pbot.tg;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.File;
import java.util.Optional;

public interface TelegramSender {

  Optional<ChatMember> getChatMember(String botId, long chatId, int userId);

  void deleteMessage(String botId, String chatId, int messageId);

  void sendMessage(String botId, String chatId, String message);

  void sendMessage(String botId, String chatId, String message, boolean disableNotification);

  String sendFile(String botId, String chatId, File file);

  String sendStringAsFile(String botId, String chatId, String fileName, String string);


  Optional<Chat> getChat(String botId, long chatId);

  int getChatMemberCount(String botId, long chatId);

  User getMe(String botToken);
}
