package by.kobyzau.tg.bot.pbot.service;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;


public interface TelegramService {

  Optional<ChatMember> getChatMember(long chatId, long userId);

  List<Long> getChatIds();

  Optional<Chat> getChat(long chatId);

  Optional<Integer> getChatMemberCount(long chatId);

  Optional<User> getMe();
}
