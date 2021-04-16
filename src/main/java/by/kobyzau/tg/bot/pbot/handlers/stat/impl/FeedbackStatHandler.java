package by.kobyzau.tg.bot.pbot.handlers.stat.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.stat.StatHandler;
import by.kobyzau.tg.bot.pbot.model.Feedback;
import by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.service.FeedbackService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FeedbackStatHandler implements StatHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private TelegramService telegramService;
  @Autowired private FeedbackService feedbackService;

  @Override
  public void printStat(long chatId) {
    List<Long> chatIds = telegramService.getChatIds();
    botActionCollector.text(chatId, new ParametizedText("{0} чатов", new IntText(chatIds.size())));
    for (long id : chatIds) {
      printChatInfo(chatId, id);
    }
  }

  private void printChatInfo(long sendToChatId, long chatId) {
    List<Feedback> feedbacks =
        feedbackService.getFeedbacks().stream()
            .filter(f -> f.getChatId() == chatId)
            .filter(f -> f.getFeedbackType() == FeedbackType.VERSION)
            .collect(Collectors.toList());
    TextBuilder tx = new TextBuilder();
    tx.append(new SimpleText("Chat ID " + chatId)).append(new NewLineText());
    Optional<Chat> chat = telegramService.getChat(chatId);
    chat.ifPresent(
        c ->
            tx.append(new SimpleText("Chat " + chatId))
                .append(new SimpleText(" "))
                .append(getChatName(c))
                .append(new NewLineText()));
    for (FeedbackEmojiType emojiType : FeedbackEmojiType.values()) {
      tx.append(new NewLineText());
      tx.append(new SimpleText(emojiType.getEmoji()))
          .append(new SpaceText())
          .append(
              new LongText(feedbacks.stream().filter(f -> f.getEmojiType() == emojiType).count()));
    }

    botActionCollector.wait(sendToChatId, 1, ChatAction.TYPING);
    botActionCollector.text(sendToChatId, tx);
  }

  private Text getChatName(Chat chat) {
    return Optional.ofNullable(chat)
        .map(Chat::getTitle)
        .filter(StringUtil::isNotBlank)
        .map(TGUtil::escapeHTML)
        .map(SimpleText::new)
        .orElse(new SimpleText("..."));
  }

  @Override
  public StatType getType() {
    return StatType.FEEDBACK;
  }
}
