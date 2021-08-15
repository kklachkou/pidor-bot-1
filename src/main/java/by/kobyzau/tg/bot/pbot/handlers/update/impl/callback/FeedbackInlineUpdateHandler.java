package by.kobyzau.tg.bot.pbot.handlers.update.impl.callback;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.CallbackUpdateHandler;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandlerStage;
import by.kobyzau.tg.bot.pbot.model.Feedback;
import by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.dto.FeedbackInlineDto;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.service.FeedbackService;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageReplyMarkupBotAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType.FEEDBACK;

@Component
public class FeedbackInlineUpdateHandler extends CallbackUpdateHandler<FeedbackInlineDto> {

  @Autowired private FeedbackService feedbackService;
  @Autowired private BotActionCollector botActionCollector;

  @Override
  protected Class<FeedbackInlineDto> getDtoType() {
    return FeedbackInlineDto.class;
  }

  @Override
  protected SerializableInlineType getSerializableType() {
    return SerializableInlineType.FEEDBACK;
  }

  @Override
  protected void handleCallback(Update update, FeedbackInlineDto dto) {
    CallbackQuery callbackQuery = update.getCallbackQuery();
    User calledUser = callbackQuery.getFrom();
    Message prevMessage = callbackQuery.getMessage();
    int messageId = prevMessage.getMessageId();
    long userId = calledUser.getId();
    long chatId = prevMessage.getChatId();
    feedbackService.addUniqueFeedback(
            new Feedback(chatId, userId, messageId, dto.getEmojiType(), dto.getType()));
    String requestId = UUID.randomUUID().toString().substring(FEEDBACK.getIdSize());
    List<InlineKeyboardButton> buttons =
            dto.getType().getEmojiTypeList().stream()
                    .map(
                            emojiType ->
                                    InlineKeyboardButton.builder()
                                            .text(
                                                    emojiType.getEmoji()
                                                            + getNumVotesCounter(
                                                            chatId, messageId, dto.getType(), emojiType))
                                            .callbackData(
                                                    StringUtil.serialize(
                                                            new FeedbackInlineDto(requestId, dto.getType(), emojiType)))
                                            .build())
                    .collect(Collectors.toList());
    InlineKeyboardMarkup.InlineKeyboardMarkupBuilder keyboardMarkupBuilder =
            InlineKeyboardMarkup.builder().keyboardRow(buttons);
    botActionCollector.add(
            new EditMessageReplyMarkupBotAction(chatId, messageId, keyboardMarkupBuilder.build()));
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
