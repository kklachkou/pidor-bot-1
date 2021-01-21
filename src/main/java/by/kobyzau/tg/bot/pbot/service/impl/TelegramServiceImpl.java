package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.TelegramSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TelegramServiceImpl implements TelegramService {
  @Autowired private TelegramSender telegramSender;
  @Autowired private BotService botService;

  @Autowired private PidorRepository pidorRepository;

  @Value("${bot.token}")
  private String botToken;

  @Autowired private Logger logger;

  @Override
  public Optional<ChatMember> getChatMember(long chatId, int userId) {
    return telegramSender.getChatMember(botToken, chatId, userId);
  }

  @Override
  public List<Long> getChatIds() {
    return pidorRepository.getAll().stream()
        .map(Pidor::getChatId)
        .distinct()
        .filter(botService::isBotPartOfChat)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Chat> getChat(long chatId) {
    return telegramSender.getChat(botToken, chatId);
  }

  @Override
  public Optional<Integer> getChatMemberCount(long chatId) {
    try {
      return Optional.of(telegramSender.getChatMemberCount(botToken, chatId));
    } catch (Exception e) {
      logger.error("Cannot getChatMember for chat " + chatId, e);
      return Optional.empty();
    }
  }

  @Override
  public Optional<User> getMe() {
    try {
      return Optional.of(telegramSender.getMe(botToken));
    } catch (Exception e) {
      logger.error("Cannot getMe()", e);
      return Optional.empty();
    }
  }

  @Override
  public void deleteMessage(long chatId, int messageId) {
    try {
      telegramSender.deleteMessage(botToken, String.valueOf(chatId), messageId);
    } catch (Exception e) {
      logger.error("Cannot delete message " + messageId + " from chat " + chatId, e);
    }
  }
}
