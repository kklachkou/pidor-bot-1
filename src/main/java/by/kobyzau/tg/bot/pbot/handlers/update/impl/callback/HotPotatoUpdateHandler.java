package by.kobyzau.tg.bot.pbot.handlers.update.impl.callback;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.CallbackUpdateHandler;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.HotPotatoDto;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.program.text.BoldText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.TimeLeftText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.HotPotatoesService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.AnswerCallbackBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SimpleBotAction;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.HotPotatoUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class HotPotatoUpdateHandler extends CallbackUpdateHandler<HotPotatoDto> {

  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private HotPotatoesService hotPotatoesService;
  @Autowired private PidorService pidorService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private HotPotatoUtil hotPotatoUtil;

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
      botActionCollector.add(
          new AnswerCallbackBotAction(
              chatId,
              update.getCallbackQuery().getId(),
              new SimpleText("Пидора сегодня уже нашли")));
      return;
    }
    Optional<Pidor> lastTaker = hotPotatoesService.getLastTaker(DateUtil.now(), chatId);
    if (lastTaker.isPresent() && Objects.equals(user.getId(), lastTaker.get().getTgId())) {
      List<Pidor> pidors =
          pidorService.getByChat(chatId).stream()
              .filter(p -> p.getTgId() != lastTaker.get().getTgId())
              .collect(Collectors.toList());
      Pidor newTaker = CollectionUtil.getRandomValue(pidors);
      LocalDateTime newDeadline = hotPotatoesService.setNewTaker(newTaker);
      botActionCollector.add(
          new SimpleBotAction<>(
              EditMessageText.builder()
                  .messageId(message.getMessageId())
                  .chatId(String.valueOf(chatId))
                  .text(
                      new ParametizedText(
                              "Карочешка переброшена с {0} на {1}",
                              new ShortNamePidorText(lastTaker.get()),
                              new ShortNamePidorText(newTaker))
                          .text())
                  .replyMarkup(InlineKeyboardMarkup.builder().clearKeyboard().build())
                  .build()));
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
                                  .text("Перекинуть картошку")
                                  .callbackData(StringUtil.serialize(new HotPotatoDto()))
                                  .build()))
                      .build()));
    } else if (lastTaker.isPresent()) {
      botActionCollector.add(
          new AnswerCallbackBotAction(
              chatId,
              update.getCallbackQuery().getId(),
              new SimpleText("Горячая картошечка не у тебя")));
    }
  }
}
