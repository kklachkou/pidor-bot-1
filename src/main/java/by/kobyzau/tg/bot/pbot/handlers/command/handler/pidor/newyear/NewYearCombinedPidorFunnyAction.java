package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.newyear;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.PidorFunnyAction;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.RepeatPidorProcessor;
import by.kobyzau.tg.bot.pbot.handlers.update.fun.Intro;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.PrioritySelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.FeedbackService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.ReplyKeyboardBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendStickerBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Optional;

@Profile("new-year")
@Component
public class NewYearCombinedPidorFunnyAction implements PidorFunnyAction {

  @Autowired
  @Qualifier("NewYearPidorOfTheDayIntro")
  private Intro intro;

  @Autowired private FeedbackService feedbackService;

  private final Selection<String> finalMessage;

  @Autowired private RepeatPidorProcessor repeatPidorProcessor;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private BotService botService;

  public NewYearCombinedPidorFunnyAction() {
    this.finalMessage =
        new ConsistentSelection<>(
            "???????????????? ??????????????, {0} - ???? ?????????????? ?????????? ??????!",
            "{0} - ???? ?????????? ???????? ???????????????? ??????????-??????????, ?????????? ????????????????????, ?????? ???? ?????????????? ?????????? ??????",
            "???????? ??????-???????????? ???????????????? ?????? ???????????????? ?? ???????????????? ?????? ?????????????? ?????????? ??????, ?? ???????????? {0}",
            "???????? ??????-???????????? ???????????????? ?????????????? ?????????????? ????????-???????????? ?????????????? ??????, ?? ???????????? {0} - ???????????? ?????????????? ??????",
            "?? ???????????????????? ?????????? ?????? ??????????????... ?? ??????, ????????????, ??????????-???????? ?????????? - {0}",
            "??????, ???? ???????????????????? ????????????! ?? ?????????????? ?????????? ?????? ???? - {0}",
            "?? ?????????????? ?????? ???????????? ?????????? ?????? {0}",
            "?????????????? ???????????????? ???? ?????????????????????? ????????? {0} - ?????? ???????? ?????????????????? ?? ?????????????? ?????????????? ??????",
            "?????????? ?????? ????????????????, {0} - ???? ?????????? ?????????? ?????????????????????? ?????????????? ??????!",
            "???????????? ????????????????????, {0} - ?????????????????? ????????????????????, ???? ?????????? ??????!",
            "???????????????? ???????????????????? ????????????????????, {0} ?????????????? ?????????? ??????",
            "???????? ???? ????????????, {0} - ??????????, ???????????? ???? ?????????? ???????",
            "???????????????????? ????????????????, ?? ?????? ?????????????? ?????????? - {0}",
            "?? ???????????????? ???????????? ???????????????????? - {0}");
  }

  @Override
  public PrioritySelection.Priority getPriority() {
    return PrioritySelection.Priority.HIGHEST;
  }

  @Override
  public void processFunnyAction(long chatId, Pidor pidorOfTheDay) {

    botActionCollector.sticker(chatId, StickerType.LOOKING_PIDOR.getRandom());
    intro.sendIntro(chatId);
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.NEW_YEAR.getRandom());
    botActionCollector.wait(chatId, ChatAction.TYPING);

    Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidorOfTheDay.getSticker());
    if (pidorSticker.isPresent()) {
      botActionCollector.add(
          new ReplyKeyboardBotAction(
              chatId,
              new ParametizedText(finalMessage.next(), new FullNamePidorText(pidorOfTheDay)),
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
                  new ParametizedText(finalMessage.next(), new FullNamePidorText(pidorOfTheDay)),
                  InlineKeyboardMarkup.builder()
                      .keyboardRow(feedbackService.getButtons(FeedbackType.PIDOR))
                      .build(),
                  null)));
      botActionCollector.wait(chatId, ChatAction.TYPING);
    }
    botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());
    repeatPidorProcessor.checkPidorRepeat(pidorOfTheDay);
  }
}
