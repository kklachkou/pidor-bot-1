package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.model.Feedback;
import by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.dto.FeedbackInlineDto;
import by.kobyzau.tg.bot.pbot.repository.feedback.FeedbackRepository;
import by.kobyzau.tg.bot.pbot.service.FeedbackService;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType.FEEDBACK;

@Service
public class FeedbackServiceImpl implements FeedbackService {

  @Autowired private FeedbackRepository feedbackRepository;

  @Override
  public List<InlineKeyboardButton> getButtons(FeedbackType feedbackType) {
    String requestId = UUID.randomUUID().toString().substring(FEEDBACK.getIdSize());
    return Stream.of(FeedbackEmojiType.values())
        .map(
            type ->
                InlineKeyboardButton.builder()
                    .text(type.getEmoji())
                    .callbackData(
                        StringUtil.serialize(new FeedbackInlineDto(requestId, feedbackType, type)))
                    .build())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void addUniqueFeedback(Feedback feedback) {
    feedbackRepository.getByChat(feedback.getChatId()).stream()
        .filter(f -> Objects.equals(f.getMessageId(), feedback.getMessageId()))
        .filter(f -> Objects.equals(f.getFeedbackType(), feedback.getFeedbackType()))
        .filter(f -> Objects.equals(f.getPlayerTgId(), feedback.getPlayerTgId()))
        .map(Feedback::getId)
        .forEach(feedbackRepository::delete);
    feedbackRepository.create(feedback);
  }

  @Override
  public List<Feedback> getFeedbacks(
      long chatId, int messageId, FeedbackType feedbackType, FeedbackEmojiType emojiType) {
    return feedbackRepository.getByChat(chatId).stream()
        .filter(f -> Objects.equals(f.getEmojiType(), emojiType))
        .filter(f -> Objects.equals(f.getMessageId(), messageId))
        .filter(f -> Objects.equals(f.getFeedbackType(), feedbackType))
        .collect(Collectors.toList());
  }

  @Override
  public List<Feedback> getFeedbacks() {
    return feedbackRepository.getAll();
  }

  @Override
  public void removeAllFeedbacks(FeedbackType feedbackType) {
    feedbackRepository.getAll().stream()
        .filter(f -> f.getFeedbackType() == feedbackType)
        .map(Feedback::getId)
        .forEach(feedbackRepository::delete);
  }
}
