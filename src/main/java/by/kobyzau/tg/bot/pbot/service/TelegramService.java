package by.kobyzau.tg.bot.pbot.service;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;


public interface TelegramService {

  Optional<ChatMember> getChatMember(long chatId, int userId);

  List<Long> getChatIds();

  void deleteMessage(long chatId, int messageId);

  Optional<Chat> getChat(long chatId);

  Optional<Integer> getChatMemberCount(long chatId);

  Optional<User> getMe();
}
