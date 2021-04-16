package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.Feedback;
import by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public interface FeedbackService {

  List<InlineKeyboardButton> getButtons(FeedbackType feedbackType);

  void addUniqueFeedback(Feedback feedback);

  List<Feedback> getFeedbacks(
          long chatId, int messageId, FeedbackType feedbackType, FeedbackEmojiType emojiType);

  List<Feedback> getFeedbacks();

  void removeAllFeedbacks(FeedbackType feedbackType);
}
