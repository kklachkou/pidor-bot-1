package by.kobyzau.tg.bot.pbot.handlers.update.impl.callback;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.helper.BlackBoxHelper;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.CallbackUpdateHandler;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.OpenBlackBoxDto;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.program.text.ItalicText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.sender.BotSender;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.SimpleBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.helper.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

import static by.kobyzau.tg.bot.pbot.sender.methods.SendMethod.method;

@Component
public class OpenBlackBoxUpdateHandler extends CallbackUpdateHandler<OpenBlackBoxDto> {

  @Autowired private PidorService pidorService;
  @Autowired private UserArtifactService userArtifactService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private BotSender directPidorBotSender;
  @Autowired private CollectionHelper collectionHelper;
  @Autowired private BlackBoxHelper blackBoxHelper;

  @Override
  protected Class<OpenBlackBoxDto> getDtoType() {
    return OpenBlackBoxDto.class;
  }

  @Override
  protected SerializableInlineType getSerializableType() {
    return SerializableInlineType.OPEN_BLACK_BOX;
  }

  @Override
  protected void handleCallback(Update update, OpenBlackBoxDto dto) {

    CallbackQuery callbackQuery = update.getCallbackQuery();
    long userId = callbackQuery.getFrom().getId();
    long chatId = callbackQuery.getMessage().getChatId();
    Optional<Pidor> pidor = pidorService.getPidor(chatId, userId);
    if (!pidor.isPresent()) {
      directPidorBotSender.send(
          chatId,
          method(
              AnswerCallbackQuery.builder()
                  .text("Вы не зарегистрированы в игре")
                  .callbackQueryId(callbackQuery.getId())
                  .showAlert(true)
                  .cacheTime(5)
                  .build()));
      return;
    }
    if (blackBoxHelper.isUserOpenedBox(chatId, userId)) {
      directPidorBotSender.send(
          chatId,
          method(
              AnswerCallbackQuery.builder()
                  .text("Ты сегодня уже получал артефакт")
                  .callbackQueryId(callbackQuery.getId())
                  .showAlert(true)
                  .build()));
      return;
    }
    if (blackBoxHelper.checkRequest(chatId, userId, dto.getId())) {
      directPidorBotSender.send(
          chatId,
          method(
              AnswerCallbackQuery.builder()
                  .text("Кто-то открыл ящик раньше тебя, попробуй повторить")
                  .callbackQueryId(callbackQuery.getId())
                  .showAlert(true)
                  .build()));
      return;
    }
    int numArtifactsPerDay = blackBoxHelper.getNumArtifactsPerDay(chatId);
    int handledNum = blackBoxHelper.getHandledNum(chatId);
    int artifactsLeft = numArtifactsPerDay - handledNum;

    if (artifactsLeft > 0) {
      String requestId = blackBoxHelper.getRequestId();
      botActionCollector.add(
          new SimpleBotAction<>(
              chatId,
              EditMessageReplyMarkup.builder()
                  .replyMarkup(
                      InlineKeyboardMarkup.builder()
                          .keyboardRow(
                              Collections.singletonList(
                                  InlineKeyboardButton.builder()
                                      .text("Открыть подарок (" + artifactsLeft + ")")
                                      .callbackData(
                                          StringUtil.serialize(new OpenBlackBoxDto(requestId)))
                                      .build()))
                          .build())
                  .chatId(String.valueOf(chatId))
                  .messageId(callbackQuery.getMessage().getMessageId())
                  .build(),
              true));
    } else {
      botActionCollector.add(
          new SimpleBotAction<>(
              chatId,
              EditMessageReplyMarkup.builder()
                  .replyMarkup(InlineKeyboardMarkup.builder().clearKeyboard().build())
                  .chatId(String.valueOf(chatId))
                  .messageId(callbackQuery.getMessage().getMessageId())
                  .build(),
              true));
    }
    List<ArtifactType> artifactTypeList = blackBoxHelper.getArtifactsForBox(chatId);
    ArtifactType artifactType = collectionHelper.getRandomValue(artifactTypeList);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0} взял яйца в кулак и открыл чёрный ящик", new FullNamePidorText(pidor.get())));
    userArtifactService.addArtifact(chatId, userId, artifactType, DateUtil.now().plusDays(1));
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0} - А в ящике лежит {1} {2}!\n{3}",
            new ShortNamePidorText(pidor.get()),
            new ItalicText(artifactType.getName()),
            new SimpleText(artifactType.getEmoji()),
            new SimpleText(artifactType.getDesc())));
  }
}
