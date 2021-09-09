package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.RepeatPidorProcessor;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
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
public class NewYearDateBasedPidorFunnyAction implements DateBasePidorFunnyAction {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private RepeatPidorProcessor repeatPidorProcessor;
  @Autowired private FeedbackService feedbackService;

  @Override
  public void processFunnyAction(long chatId, Pidor pidorOfTheDay) {
    botActionCollector.sticker(chatId, StickerType.NEW_YEAR.getRandom());
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Наконец 2020-й закончился, ну его в жопу!"));
    botActionCollector.sticker(chatId, StickerType.CONGRATULATION.getRandom());
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Много пидоров было найдено"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Много пидоров ещё предстоит найти"));
    botActionCollector.sticker(chatId, StickerType.LOL.getRandom());
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Давай-те начнем 2021 год с хороших новостей!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.NEW_YEAR.getRandom());
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("А хорошие новости я всегда найду!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "Давайте поздравим {0} - нашего первооткрывателя, "
                + "нашего первого заднепроходчика, первого после Тима Кука!",
            new FullNamePidorText(pidorOfTheDay)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidorOfTheDay.getSticker());
    if (pidorSticker.isPresent()) {
      botActionCollector.add(
          new ReplyKeyboardBotAction(
              chatId,
              new ParametizedText(
                  "Давайте поздравим {0} - нашего первооткрывателя, "
                      + "нашего первого заднепроходчика, первого после Тима Кука!",
                  new FullNamePidorText(pidorOfTheDay)),
              InlineKeyboardMarkup.builder()
                  .keyboardRow(feedbackService.getButtons(FeedbackType.PIDOR))
                  .build(),
              null));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new SendStickerBotAction(chatId, pidorSticker.get().getRandom())));
      botActionCollector.wait(chatId, ChatAction.TYPING);
    } else {
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new ReplyKeyboardBotAction(
                  chatId,
                  new ParametizedText(
                      "Давайте поздравим {0} - нашего первооткрывателя, "
                          + "нашего первого заднепроходчика, первого после Тима Кука!",
                      new FullNamePidorText(pidorOfTheDay)),
                  InlineKeyboardMarkup.builder()
                      .keyboardRow(feedbackService.getButtons(FeedbackType.PIDOR))
                      .build(),
                  null)));
      botActionCollector.wait(chatId, ChatAction.TYPING);
    }

    botActionCollector.text(chatId, new SimpleText("Ведь, как говорится, первый раз..."));
    botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());
    repeatPidorProcessor.checkPidorRepeat(pidorOfTheDay);
  }

  @Override
  public boolean testDate(LocalDate date) {
    return date.getDayOfYear() == 1;
  }
}
