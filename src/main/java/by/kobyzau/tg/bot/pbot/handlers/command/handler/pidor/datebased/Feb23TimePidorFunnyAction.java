package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.RepeatPidorProcessor;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.service.FeedbackService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.ReplyKeyboardBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

@Component
public class Feb23TimePidorFunnyAction implements DateBasePidorFunnyAction {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private RepeatPidorProcessor repeatPidorProcessor;
  @Autowired private FeedbackService feedbackService;

  @Override
  public void processFunnyAction(long chatId, Pidor pidorOfTheDay) {

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(chatId, "Гыыыаа!");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(chatId, "Ну что, щенки, не служил не мужЫк!");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(chatId, "Буээээ!");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(chatId, "Иди в армию! Там из тебя мужЫка сделают!");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(chatId, "Ыыыыыыы!");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(
        chatId, "Отдать долг Родине! Ты должен служить Отечеству!");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(chatId, "Ииыиыиии!");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.add(
        new ReplyKeyboardBotAction(
            chatId,
            new ParametizedText(
                new ParametizedText(
                    "Упал - отжался, {0}. Твой праздник - 8 марта",
                    new FullNamePidorText(pidorOfTheDay)),
                new FullNamePidorText(pidorOfTheDay)),
            InlineKeyboardMarkup.builder()
                .keyboardRow(feedbackService.getButtons(FeedbackType.PIDOR))
                .build(),
            null));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidorOfTheDay.getSticker());
    pidorSticker.ifPresent(
        stickerType -> botActionCollector.sticker(chatId, stickerType.getRandom()));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());
    repeatPidorProcessor.checkPidorRepeat(pidorOfTheDay);
  }

  @Override
  public boolean testDate(LocalDate date) {
    return date.getMonth().equals(Month.FEBRUARY) && date.getDayOfMonth() == 23;
  }
}
