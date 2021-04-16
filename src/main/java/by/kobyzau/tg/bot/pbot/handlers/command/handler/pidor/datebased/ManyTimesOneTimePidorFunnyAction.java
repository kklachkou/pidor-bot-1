package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.RepeatPidorProcessor;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.FeedbackService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.ReplyKeyboardBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendStickerBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class ManyTimesOneTimePidorFunnyAction implements DateBasePidorFunnyAction {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private BotService botService;

  @Autowired private FeedbackService feedbackService;
  @Autowired private RepeatPidorProcessor repeatPidorProcessor;

  @Override
  public void processFunnyAction(long chatId, Pidor pidorOfTheDay) {
    ShortNamePidorText pidorName = new ShortNamePidorText(pidorOfTheDay);
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new ParametizedText("Не буду церимониться, сегодня пидор - {0}", pidorName));
    botActionCollector.wait(chatId, 1, ChatAction.TYPING);
    Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidorOfTheDay.getSticker());
    pidorSticker.ifPresent(
        stickerType ->
            botActionCollector.add(
                new PingMessageWrapperBotAction(
                    new SendStickerBotAction(chatId, stickerType.getRandom()),
                    botService.canPinMessage(chatId))));
    botActionCollector.wait(chatId, 5, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Блин, а красиво звучит!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new ParametizedText("Хочу еще раз сказать! {0} - ты пидор!", pidorName));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new ParametizedText("Ха-ха-ха, {0} - ты пидор дня!", pidorName));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.LOL.getRandom());
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Давно так не угарал)"));
    botActionCollector.wait(chatId, ChatAction.TYPING);

    botActionCollector.text(chatId, new SimpleText("А кто сегодня пидор дня?"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new ParametizedText("Правильно! {0} сегодня наш пидорок", pidorName));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.LOVE.getRandom());
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new ParametizedText("А {0} - пидор дня, а {1} - пидор дня", pidorName, pidorName));
    botActionCollector.wait(chatId, ChatAction.TYPING);

    botActionCollector.text(chatId, new SimpleText("Всё, нужно остановиться..."));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    if (pidorSticker.isPresent()) {
      botActionCollector.add(
          new ReplyKeyboardBotAction(
              chatId,
              new ParametizedText(
                  new ParametizedText(
                      "Ну и на последок - {0} - ты пидор дня:)",
                      new FullNamePidorText(pidorOfTheDay)),
                  new FullNamePidorText(pidorOfTheDay)),
              InlineKeyboardMarkup.builder()
                  .keyboardRow(feedbackService.getButtons(FeedbackType.PIDOR))
                  .build(),
              null));
    } else {
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new ReplyKeyboardBotAction(
                  chatId,
                  new ParametizedText(
                      new ParametizedText(
                          "Ну и на последок - {0} - ты пидор дня:)",
                          new FullNamePidorText(pidorOfTheDay)),
                      new FullNamePidorText(pidorOfTheDay)),
                  InlineKeyboardMarkup.builder()
                      .keyboardRow(feedbackService.getButtons(FeedbackType.PIDOR))
                      .build(),
                  null),
              botService.canPinMessage(chatId)));
    }

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());
    repeatPidorProcessor.checkPidorRepeat(pidorOfTheDay);
  }

  @Override
  public boolean testDate(LocalDate date) {
    return date.getDayOfYear() % 20 == 0;
  }
}
