package by.kobyzau.tg.bot.pbot.handlers.update.impl.callback;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.CallbackUpdateHandler;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.OpenBlackBoxDto;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.sender.BotSender;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.SimpleBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.helper.CollectionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Arrays;
import java.util.Optional;

import static by.kobyzau.tg.bot.pbot.sender.methods.SendMethod.method;

@Component
public class OpenBlackBoxUpdateHandler extends CallbackUpdateHandler<OpenBlackBoxDto> {

  @Autowired private PidorService pidorService;
  @Autowired private UserArtifactService userArtifactService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private BotSender directPidorBotSender;
  @Autowired private CollectionHelper collectionHelper;

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

    botActionCollector.add(
        new SimpleBotAction<>(
            chatId,
            EditMessageReplyMarkup.builder()
                .replyMarkup(InlineKeyboardMarkup.builder().clearKeyboard().build())
                .chatId(String.valueOf(chatId))
                .messageId(callbackQuery.getMessage().getMessageId())
                .build(),
            true));

    ArtifactType artifactType =
        collectionHelper.getRandomValue(Arrays.asList(ArtifactType.values()));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0} взял яйца в кулак и открыл чёрный ящик", new FullNamePidorText(pidor.get())));
    userArtifactService.addArtifact(chatId, userId, artifactType, DateUtil.now().plusDays(1));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "А в ящике лежит {0} {1}!\n{2}",
            new SimpleText(artifactType.getName()),
            new SimpleText(artifactType.getEmoji()),
            new SimpleText(artifactType.getDesc())));
  }
}
