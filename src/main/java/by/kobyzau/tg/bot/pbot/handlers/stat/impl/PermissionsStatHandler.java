package by.kobyzau.tg.bot.pbot.handlers.stat.impl;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.stat.StatHandler;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.util.List;
import java.util.Optional;

@Component
public class PermissionsStatHandler implements StatHandler {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private TelegramService telegramService;
  @Autowired private Bot bot;

  @Override
  public void printStat(long chatId) {
    List<Long> chatIds = telegramService.getChatIds();
    botActionCollector.text(chatId, new ParametizedText("{0} чатов", new IntText(chatIds.size())));
    for (long id : chatIds) {
      printChatInfo(chatId, id);
    }
  }

  private void printChatInfo(long sendToChatId, long chatId) {
    Optional<ChatMember> botMember = getBotMember(chatId);
    if (!botMember.isPresent()) {
      return;
    }
    TextBuilder tx = new TextBuilder();
    Optional<Chat> chat = telegramService.getChat(chatId);
    chat.ifPresent(
        c ->
            tx.append(new SimpleText("Chat " + chatId))
                .append(new SimpleText(" "))
                .append(getChatName(c))
                .append(new NewLineText()));
    tx.append(
            new SimpleText(
                "Can pin: "
                    + (TGUtil.canPinMessage(botMember.get()) ? "\uD83D\uDFE2" : "\uD83D\uDD34")))
        .append(new NewLineText())
        .append(
            new SimpleText(
                "Can delete: "
                    + (TGUtil.canDeleteMessage(botMember.get()) ? "\uD83D\uDFE2" : "\uD83D\uDD34")))
        .append(new NewLineText())
        .append(
            new SimpleText(
                "Can send Other Message: "
                    + (TGUtil.canSendOtherMessages(botMember.get())
                        ? "\uD83D\uDFE2"
                        : "\uD83D\uDD34")));

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

  private Optional<ChatMember> getBotMember(long chatId) {
    try {
      User botUser = bot.execute(GetMe.builder().build());
      ChatMember botMember =
          bot.execute(
              GetChatMember.builder()
                  .chatId(String.valueOf(chatId))
                  .userId(botUser.getId())
                  .build());
      return Optional.of(botMember);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public StatType getType() {
    return StatType.PERMISSIONS;
  }
}
