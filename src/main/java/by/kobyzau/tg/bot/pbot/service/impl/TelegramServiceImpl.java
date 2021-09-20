package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.bots.PidorBot;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMemberCount;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TelegramServiceImpl implements TelegramService {
  @Autowired private PidorBot pidorBot;
  @Autowired private BotService botService;

  @Autowired private PidorRepository pidorRepository;

  @Autowired private Logger logger;

  @Override
  public Optional<ChatMember> getChatMember(long chatId, long userId) {
    try {
      return Optional.of(
          pidorBot.execute(
              GetChatMember.builder().chatId(String.valueOf(chatId)).userId(userId).build()));
    } catch (TelegramApiRequestException e) {
      Integer errorCode = e.getErrorCode();
      if (errorCode != null && errorCode.equals(400)) {
        logger.debug(
            "Cannot get Chat Member for chat <pre>"
                + chatId
                + "</pre> and user "
                + userId
                + ":\n"
                + e.getMessage());
      } else {
        logger.error(
            "Cannot get Chat Member for chat <pre>"
                + chatId
                + "</pre> and user "
                + userId
                + ":\n"
                + e.getMessage(),
            e);
      }
      return Optional.empty();
    } catch (TelegramApiException e) {
      logger.error(
          "Cannot get Chat Member for chat <pre>"
              + chatId
              + "</pre> and user "
              + userId
              + ":\n"
              + e.getMessage(),
          e);
      return Optional.empty();
    }
  }

  @Override
  public List<Long> getChatIds() {
    return pidorRepository.getChatIdsWithPidors().stream()
        .distinct()
        .filter(botService::isBotPartOfChat)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Chat> getChat(long chatId) {
    try {
      return Optional.of(
          pidorBot.execute(GetChat.builder().chatId(String.valueOf(chatId)).build()));
    } catch (TelegramApiException e) {
      logger.debug("Cannot get Chat for chat <pre>" + chatId + "</pre>:\n" + e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public Optional<Integer> getChatMemberCount(long chatId) {
    try {
      return Optional.of(
          pidorBot.execute(GetChatMemberCount.builder().chatId(String.valueOf(chatId)).build()));
    } catch (TelegramApiException e) {
      logger.debug("Cannot get Chat Member Count for chat <pre>" + chatId + "</pre>\n\n" + e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  @Cacheable("botUser")
  public Optional<User> getMe() {
    try {
      return Optional.of(pidorBot.execute(GetMe.builder().build()));
    } catch (TelegramApiException e) {
      logger.error("Cannot get Me", e);
      return Optional.empty();
    }
  }

  @Override
  public void deleteMessage(long chatId, int messageId) {
    try {
      pidorBot.execute(
          DeleteMessage.builder().chatId(String.valueOf(chatId)).messageId(messageId).build());
    } catch (Exception e) {
      logger.debug(
          "Cannot delete message " + messageId + " from chat " + chatId + "\n\n" + e.getMessage());
    }
  }
}
