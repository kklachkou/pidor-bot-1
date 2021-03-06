package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.RepeatPidorProcessor;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
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
public class ValentineDayTimePidorFunnyAction implements DateBasePidorFunnyAction {

  @Autowired private RepeatPidorProcessor repeatPidorProcessor;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private FeedbackService feedbackService;

  @Override
  public boolean testDate(LocalDate date) {
    return date.getDayOfMonth() == 14 && date.getMonth() == Month.FEBRUARY;
  }

  @Override
  public void processFunnyAction(long chatId, Pidor pidorOfTheDay) {
    final String kupidorPhoto =
        "AgACAgIAAxkBAAIDv15FpBTrdSRz5Ktb0gq5csoEdWJeAAJrrTEbUbYoSlfh2pBfTq3sXUfLDgAEAQADAgADeAADYjgCAAEYBA";
    Selection<String> loveSticker = new ConsistentSelection<>(StickerType.LOVE.getStickers());

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.sticker(chatId, loveSticker.next());

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(chatId, "????????????!");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(chatId, "?? ?????? ??!");

    botActionCollector.wait(chatId, ChatAction.UPLOAD_PHOTO);
    botActionCollector.photo(chatId, kupidorPhoto);

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(chatId, "?????? ?????????????????????????? ?? ???????????????? ??????????");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(
        chatId, "?? ???????????????? ???????????? ???? ???????????? ?????????????? ?????????????? ???????????? ??????????");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(chatId, "?????????????????? ?????????????? ????????????");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(chatId, "?????????? ???????????????? ??????-???? ?????????????????? ?????????? ??????????");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(chatId, "??????-??????");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.collectHTMLMessage(chatId, "Oh no");

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            new SimpleText("?? ?????? ???????????? ?????????????????? ?? ????????, {0}"),
            new FullNamePidorText(pidorOfTheDay)));

    botActionCollector.wait(chatId, ChatAction.TYPING);
    Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidorOfTheDay.getSticker());
    pidorSticker.ifPresent(
        stickerType -> botActionCollector.sticker(chatId, stickerType.getRandom()));

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.add(
        new ReplyKeyboardBotAction(
            chatId,
            new SimpleText("?? ??????????????, ?????? ????????????????:)"),
            InlineKeyboardMarkup.builder()
                .keyboardRow(feedbackService.getButtons(FeedbackType.PIDOR))
                .build(),
            null));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.sticker(chatId, loveSticker.next());
    repeatPidorProcessor.checkPidorRepeat(pidorOfTheDay);
  }
}
