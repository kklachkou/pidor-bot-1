package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.action.UnpinBotAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Service
public class BotServiceImpl implements BotService {

  @Value("${bot.token}")
  private String botToken;

  @Value("${app.admin.userId}")
  private int adminUserId;

  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public boolean canPinMessage(long chatId) {
    return telegramService
        .getMe()
        .map(User::getId)
        .map(botId -> telegramService.getChatMember(chatId, botId))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(ChatMember::getCanPinMessages)
        .orElse(false);
  }

  @Override
  public boolean isChatValid(long chatId) {
    return adminUserId == chatId
        || telegramService
            .getChat(chatId)
            .map(c -> c.isGroupChat() || c.isSuperGroupChat())
            .orElse(false);
  }

  @Override
  public boolean isBotPartOfChat(long chatId) {
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
    String botId = StringUtil.substringBefore(botToken, ":");
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
