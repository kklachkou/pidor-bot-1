package by.kobyzau.tg.bot.pbot.handlers.stat.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.stat.StatHandler;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMember;

import java.util.List;
import java.util.Optional;

@Component
public class ChatsStatHandler implements StatHandler {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private TelegramService telegramService;
  @Autowired private PidorService pidorService;
  @Autowired private BotService botService;

  @Override
  public void printStat(long chatId) {
    List<Long> chatIds = telegramService.getChatIds();
    botActionCollector.text(chatId, new ParametizedText("{0} чатов", new IntText(chatIds.size())));
    for (long id : chatIds) {
      printChatInfo(chatId, id);
    }
  }

  private void printChatInfo(long sendToChatId, long chatId) {
    TextBuilder tx = new TextBuilder();
    tx.append(new SimpleText("Chat ID " + chatId)).append(new NewLineText());
    Optional<Chat> chat = telegramService.getChat(chatId);
    chat.ifPresent(
        c ->
            tx.append(new SimpleText("Chat " + chatId))
                .append(new SimpleText(" "))
                .append(getChatName(c))
                .append(new NewLineText()));

    List<Pidor> pidors = pidorService.getByChat(chatId);
    Integer membersCount = telegramService.getChatMemberCount(chatId).orElse(-1);
    tx.append(
            new ParametizedText(
                "{0} registered from {1}", new IntText(pidors.size()), new IntText(membersCount)))
        .append(new NewLineText());

    for (Pidor pidor : pidors) {
      tx.append(new IntText(pidor.getTgId()))
          .append(new SimpleText(": "))
          .append(new FullNamePidorText(pidor))
          .append(new SimpleText(" "))
          .append(new DateText(pidor.getUsernameLastUpdated()))
          .append(new NewLineText());
      telegramService
          .getChatMember(chatId, pidor.getTgId())
          .map(ChatMember::getStatus)
          .map(SimpleText::new)
          .ifPresent(s -> tx.append(s).append(new NewLineText()));
    }
    botActionCollector.wait(sendToChatId, 1, ChatAction.TYPING);
    botActionCollector.text(sendToChatId, tx);
  }

  private Text getChatName(Chat chat) {
    return Optional.of(chat)
        .map(Chat::getTitle)
        .filter(StringUtil::isNotBlank)
        .map(TGUtil::escapeHTML)
        .map(SimpleText::new)
        .orElse(new SimpleText("..."));
  }

  @Override
  public StatType getType() {
    return StatType.CHATS;
  }
}
