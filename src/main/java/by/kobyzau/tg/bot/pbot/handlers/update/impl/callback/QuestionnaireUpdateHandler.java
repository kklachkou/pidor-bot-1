package by.kobyzau.tg.bot.pbot.handlers.update.impl.callback;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.CallbackUpdateHandler;
import by.kobyzau.tg.bot.pbot.model.QuestionnaireAnswer;
import by.kobyzau.tg.bot.pbot.model.QuestionnaireType;
import by.kobyzau.tg.bot.pbot.model.dto.QuestionnaireInlineDto;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.service.QuestionnaireService;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageReplyMarkupBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class QuestionnaireUpdateHandler extends CallbackUpdateHandler<QuestionnaireInlineDto> {
  @Autowired private QuestionnaireService questionnaireService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private Bot bot;
  @Autowired private Logger logger;

  @Override
  protected Class<QuestionnaireInlineDto> getDtoType() {
    return QuestionnaireInlineDto.class;
  }

  @Override
  protected SerializableInlineType getSerializableType() {
    return SerializableInlineType.QUESTIONNAIRE;
  }

  @Override
  protected void handleCallback(Update update, QuestionnaireInlineDto dto) {
    CallbackQuery callbackQuery = update.getCallbackQuery();
    User calledUser = callbackQuery.getFrom();
    Message prevMessage = callbackQuery.getMessage();
    int messageId = prevMessage.getMessageId();
    long userId = calledUser.getId();
    long chatId = prevMessage.getChatId();
    if (hasSameAnswer(chatId, userId, dto.getType(), dto.getOption())) {
      try {
        bot.execute(AnswerCallbackQuery.builder().callbackQueryId(callbackQuery.getId()).build());
      } catch (Exception e) {
        logger.error("Cannot answer callback", e);
      }
      return;
    }
    questionnaireService.addUniqueAnswer(
        new QuestionnaireAnswer(chatId, userId, dto.getType(), dto.getOption(), DateUtil.now()));
    List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
    List<String> options = dto.getType().getOptions();
    for (int i = 0; i < options.size(); i++) {
      String option = options.get(i);
      buttons.add(
          Collections.singletonList(
              InlineKeyboardButton.builder()
                  .text(option + getNumAnswersCounter(chatId, dto.getType(), i))
                  .callbackData(StringUtil.serialize(new QuestionnaireInlineDto(dto.getType(), i)))
                  .build()));
    }
    botActionCollector.add(
        new EditMessageReplyMarkupBotAction(
            chatId, messageId, InlineKeyboardMarkup.builder().keyboard(buttons).build()));
  }

  private boolean hasSameAnswer(long chatId, long userId, QuestionnaireType type, int option) {
    return questionnaireService.getAnswers(chatId).stream()
        .filter(a -> a.getType() == type)
        .filter(a -> a.getOption() == option)
        .anyMatch(a -> a.getPlayerTgId() == userId);
  }

  private String getNumAnswersCounter(
      long chatId, QuestionnaireType questionnaireType, int option) {
    long count =
        questionnaireService.getAnswers(chatId).stream()
            .filter(a -> a.getType() == questionnaireType)
            .filter(a -> a.getOption() == option)
            .count();
    if (count == 0) {
      return "";
    }
    return " " + count;
  }
}
