package by.kobyzau.tg.bot.pbot.handlers.update.impl.callback;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.CallbackUpdateHandler;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.HotPotatoDto;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.HotPotatoesService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.AnswerCallbackBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SimpleBotAction;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.HotPotatoUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class HotPotatoUpdateHandler extends CallbackUpdateHandler<HotPotatoDto> {

  @Autowired private UserArtifactService userArtifactService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private HotPotatoesService hotPotatoesService;
  @Autowired private PidorService pidorService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private HotPotatoUtil hotPotatoUtil;
  @Autowired private Logger logger;
  @Autowired private Bot bot;

  @Override
  protected Class<HotPotatoDto> getDtoType() {
    return HotPotatoDto.class;
  }

  @Override
  protected SerializableInlineType getSerializableType() {
    return SerializableInlineType.HOT_POTATOES;
  }

  @Override
  protected void handleCallback(Update update, HotPotatoDto dto) {
    User user = update.getCallbackQuery().getFrom();
    Message message = update.getCallbackQuery().getMessage();
    long chatId = message.getChatId();
    if (dailyPidorRepository.getByChatAndDate(chatId, DateUtil.now()).isPresent()) {
      try {
        bot.execute(
            AnswerCallbackQuery.builder()
                .text("???????????? ?????????????? ?????? ??????????")
                .cacheTime(60)
                .showAlert(true)
                .callbackQueryId(update.getCallbackQuery().getId())
                .build());
      } catch (Exception e) {
        logger.error("Cannot answer callback", e);
      }
      return;
    }
    Optional<Pidor> lastTaker = hotPotatoesService.getLastTaker(DateUtil.now(), chatId);
    if (lastTaker.isPresent() && Objects.equals(user.getId(), lastTaker.get().getTgId())) {
      List<Pidor> pidors = pidorService.getByChat(chatId);
      List<Pidor> pidorsForSearch = new ArrayList<>();
      for (Pidor pidor : pidors) {
        if (pidor.getId() == lastTaker.get().getId()) {
          continue;
        }
        if (userArtifactService
            .getUserArtifact(chatId, pidor.getTgId(), ArtifactType.ANTI_PIDOR)
            .isPresent()) {
          continue;
        }
        if (userArtifactService
            .getUserArtifact(chatId, pidor.getTgId(), ArtifactType.PIDOR_MAGNET)
            .isPresent()) {
          pidorsForSearch.add(pidor);
          pidorsForSearch.add(pidor);
        }
        pidorsForSearch.add(pidor);
      }

      if (CollectionUtil.isEmpty(pidorsForSearch)) {
        botActionCollector.add(
            new AnswerCallbackBotAction(
                chatId,
                update.getCallbackQuery().getId(),
                new SimpleText("???? ???????? ???????????? ??????????????")));
        return;
      }
      Pidor newTaker = CollectionUtil.getRandomValue(pidorsForSearch);
      LocalDateTime newDeadline = hotPotatoesService.setNewTaker(newTaker);
      botActionCollector.add(
          new SimpleBotAction<>(
              chatId,
              EditMessageText.builder()
                  .messageId(message.getMessageId())
                  .chatId(String.valueOf(chatId))
                  .text(
                      new ParametizedText(
                              new RandomText(
                                  "???????? ???????????????????? ?? {0}, ?????????? ?? {1}",
                                  "{0} ?????????????????? ???????????????????? ???? {1}",
                                  "{0} ???????????? ???????????????????? ???? {1}",
                                  "?????????????? ???????????????????? ?????????????????????? ?? {0} ???? {1}",
                                  "???????????????????? ?????????????????????? ?? {0} ???? {1}"),
                              new ShortNamePidorText(lastTaker.get()),
                              new ShortNamePidorText(newTaker))
                          .text())
                  .replyMarkup(InlineKeyboardMarkup.builder().clearKeyboard().build())
                  .build(),
              true));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.add(
          new SendMessageBotAction(
                  chatId,
                  new ParametizedText(
                      hotPotatoUtil.getPotatoesTakerMessage(),
                      new FullNamePidorText(newTaker),
                      new BoldText(new TimeLeftText(DateUtil.currentTime(), newDeadline))))
              .withReplyMarkup(
                  InlineKeyboardMarkup.builder()
                      .keyboardRow(
                          Collections.singletonList(
                              InlineKeyboardButton.builder()
                                  .text("???????????????????? ????????????????")
                                  .callbackData(StringUtil.serialize(new HotPotatoDto()))
                                  .build()))
                      .build()));
    } else if (lastTaker.isPresent()) {
      try {
        bot.execute(
            AnswerCallbackQuery.builder()
                .text("?????????????? ???????????????????? ???? ?? ????????")
                .cacheTime(10)
                .showAlert(true)
                .callbackQueryId(update.getCallbackQuery().getId())
                .build());
      } catch (Exception e) {
        logger.error("Cannot answer callback", e);
      }
    }
  }
}
