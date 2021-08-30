package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.bots.PidorBot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.action.UnpinBotAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberRestricted;

@Service
public class BotServiceImpl implements BotService {


  @Value("${app.admin.userId}")
  private long adminUserId;

  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private Logger logger;
  @Autowired private PidorBot pidorBot;

  @Override
  public boolean canPinMessage(long chatId) {
    Optional<ChatMember> chatMember =
        telegramService
            .getMe()
            .map(User::getId)
            .flatMap(botId -> telegramService.getChatMember(chatId, botId));
    if (!chatMember.isPresent()) {
      return false;
    }
    String status = chatMember.get().getStatus();
    switch (status) {
      case "creator":
        return true;
      case "administrator":
        ChatMemberAdministrator chatMemberAdministrator =
            (ChatMemberAdministrator) chatMember.get();
        return chatMemberAdministrator.getCanPinMessages();
      case "restricted":
        ChatMemberRestricted chatMemberRestricted = (ChatMemberRestricted) chatMember.get();
        return chatMemberRestricted.getCanPinMessages();
      default:
        return false;
    }
  }

  @Override
  public boolean canDeleteMessage(long chatId) {
    Optional<ChatMember> chatMember =
        telegramService
            .getMe()
            .map(User::getId)
            .flatMap(botId -> telegramService.getChatMember(chatId, botId));
    if (!chatMember.isPresent()) {
      return false;
    }
    String status = chatMember.get().getStatus();
    switch (status) {
      case "creator":
        return true;
      case "administrator":
        ChatMemberAdministrator chatMemberAdministrator =
            (ChatMemberAdministrator) chatMember.get();
        return chatMemberAdministrator.getCanDeleteMessages();
      default:
        return false;
    }
  }

  @Override
  @Cacheable(value = "chatValid", key = "#chatId", unless = "!#result")
  public boolean isChatValid(long chatId) {
    logger.debug("Checking is chat valid: " + chatId);
    return adminUserId == chatId
        || telegramService.getChat(chatId).map(this::isChatValid).orElse(false);
  }

  @Override
  public boolean isChatValid(Chat chat) {
    return chat != null
        && (chat.isGroupChat() || chat.isSuperGroupChat() || adminUserId == chat.getId());
  }

  @Override
  @Cacheable(value = "botPartOfChat", key = "#chatId", unless = "!#result")
  public boolean isBotPartOfChat(long chatId) {
    logger.debug("Checking bot is part of chat " + chatId);
    return TGUtil.isChatMember(
        telegramService
            .getMe()
            .map(User::getId)
            .flatMap(botId -> telegramService.getChatMember(chatId, botId)));
  }

  @Override
  public void unpinLastBotMessage(long chatId) {
    if (!canPinMessage(chatId)) {
      return;
    }
    String botId = StringUtil.substringBefore(pidorBot.getBotToken(), ":");
    telegramService
        .getChat(chatId)
        .map(Chat::getPinnedMessage)
        .filter(c -> canPinMessage(chatId))
        .filter(m -> String.valueOf(m.getFrom().getId()).equals(botId))
        .ifPresent(this::unpinMessage);
  }

  private void unpinMessage(Message message) {
    botActionCollector.add(new UnpinBotAction(message.getChatId(), message.getMessageId()));
  }
}
