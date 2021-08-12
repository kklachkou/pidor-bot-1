package by.kobyzau.tg.bot.pbot.handlers.stat.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.stat.StatHandler;
import by.kobyzau.tg.bot.pbot.model.Feedback;
import by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.program.text.LongText;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.SpaceText;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.service.FeedbackService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeedbackStatHandler implements StatHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private TelegramService telegramService;
  @Autowired private FeedbackService feedbackService;

  @Override
  public void printStat(long chatId) {
    printStat(chatId, FeedbackType.VERSION);
    printStat(chatId, FeedbackType.PIDOR);
  }

  private void printStat(long chatId, FeedbackType feedbackType) {
    botActionCollector.wait(chatId, 1, ChatAction.TYPING);
    List<Feedback> feedbacks =
        feedbackService.getFeedbacks().stream()
            .filter(f -> f.getFeedbackType() == feedbackType)
            .filter(f -> f.getUpdated().isAfter(DateUtil.now().minusDays(2)))
            .collect(Collectors.toList());
    TextBuilder tx = new TextBuilder();
    tx.append(new SimpleText(feedbackType.name())).append(new NewLineText());
    for (FeedbackEmojiType emojiType : feedbackType.getEmojiTypeList()) {
      tx.append(new NewLineText());
      tx.append(new SimpleText(emojiType.getEmoji()))
          .append(new SpaceText())
          .append(
              new LongText(feedbacks.stream().filter(f -> f.getEmojiType() == emojiType).count()));
    }

    botActionCollector.text(chatId, tx);
  }

  @Override
  public StatType getType() {
    return StatType.FEEDBACK;
  }
}
