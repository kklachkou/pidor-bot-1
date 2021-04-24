package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.Feedback;
import by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.dto.FeedbackInlineDto;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.service.FeedbackService;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageReplyMarkupBotAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType.FEEDBACK;

@Component
public class FeedbackInlineUpdateHandler implements UpdateHandler {

  @Value("${bot.pidor.username}")
  private String botUserName;

  @Autowired private FeedbackService feedbackService;
  @Autowired private BotActionCollector botActionCollector;

  @Override
  public boolean handleUpdate(Update update) {
    CallbackQuery callbackQuery = update.getCallbackQuery();
    if (callbackQuery == null) {
      return false;
    }
    Message prevMessage = callbackQuery.getMessage();
    User calledUser = callbackQuery.getFrom();
    Optional<FeedbackInlineDto> data =
        StringUtil.deserialize(callbackQuery.getData(), FeedbackInlineDto.class);
    if (prevMessage == null
        || calledUser == null
        || prevMessage.getFrom() == null
        || !data.isPresent()
        || !Objects.equals(SerializableInlineType.FEEDBACK.getIndex(), data.get().getIndex())
        || !botUserName.equalsIgnoreCase(prevMessage.getFrom().getUserName())) {
      return false;
    }
    int messageId = prevMessage.getMessageId();
    long userId = calledUser.getId();
    long chatId = prevMessage.getChatId();
    feedbackService.addUniqueFeedback(
        new Feedback(chatId, userId, messageId, data.get().getEmojiType(), data.get().getType()));
    String requestId = UUID.randomUUID().toString().substring(FEEDBACK.getIdSize());
    List<InlineKeyboardButton> buttons =
        Stream.of(FeedbackEmojiType.values())
            .map(
                emojiType ->
                    InlineKeyboardButton.builder()
                        .text(
                            emojiType.getEmoji()
                                + getNumVotesCounter(
                                    chatId, messageId, data.get().getType(), emojiType))
                        .callbackData(
                            StringUtil.serialize(
                                new FeedbackInlineDto(requestId, data.get().getType(), emojiType)))
                        .build())
            .collect(Collectors.toList());
    InlineKeyboardMarkup.InlineKeyboardMarkupBuilder keyboardMarkupBuilder =
        InlineKeyboardMarkup.builder().keyboardRow(buttons);
    botActionCollector.add(
        new EditMessageReplyMarkupBotAction(chatId, messageId, keyboardMarkupBuilder.build()));
    return true;
  }

  private String getNumVotesCounter(
      long chatId, int messageId, FeedbackType feedbackType, FeedbackEmojiType emojiType) {
    int size = feedbackService.getFeedbacks(chatId, messageId, feedbackType, emojiType).size();
    if (size == 0) {
      return "";
    }
    return " " + size;
  }
}
