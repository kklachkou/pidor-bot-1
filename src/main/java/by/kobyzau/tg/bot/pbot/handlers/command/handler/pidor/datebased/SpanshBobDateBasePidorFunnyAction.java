package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
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
public class SpanshBobDateBasePidorFunnyAction implements DateBasePidorFunnyAction {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private BotService botService;
  @Autowired private FeedbackService feedbackService;

  @Override
  public boolean testDate(LocalDate date) {
    return date.getDayOfYear() % 23 == 0;
  }

  @Override
  public void processFunnyAction(long chatId, Pidor pidorOfTheDay) {
    Text pidorName = new NotBlankText(pidorOfTheDay.getNickname(), pidorOfTheDay.getFullName());
    botActionCollector.text(chatId, new BoldText("- Вы готовы дети?"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new ItalicText("- Да, Пидор-Бот!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new BoldText("- Я вас не слышу!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new ItalicText("- Так точно, Пидор-Бот!!!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new BoldText("- Ктоооооооооооооооооооооооооооо....\n\nКто проживает на дне гей-бара???"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new ItalicText(new ParametizedText("- Пидор-{0}, розовые штаны!!", pidorName)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new BoldText("- Желтая попка, писюн без изъяна!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new ItalicText(new ParametizedText("- Пидор-{0}, розовые штаны!", pidorName)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new BoldText("- Кто губы красит всегда и везде!?!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new ItalicText(new ParametizedText("- Пидор-{0}, розовые штаны!!!", pidorName)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new BoldText("- Кто так же ловок как пидор в воде!?!?"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new ItalicText(new ParametizedText("- Пидор-{0}, розовые штаны!", pidorName)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new BoldText(new ParametizedText("- Пидор-{0}, розовые штаны!", pidorName)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new ItalicText(new ParametizedText("- Пидор-{0}, розовые штаны!!", pidorName)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.add(
        new ReplyKeyboardBotAction(
            chatId,
            new BoldText(
                new ParametizedText(
                    "- Пидоооооооооооооооор, {0}!", new FullNamePidorText(pidorOfTheDay))),
            InlineKeyboardMarkup.builder()
                .keyboardRow(feedbackService.getButtons(FeedbackType.PIDOR))
                .build(),
            null));
    Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidorOfTheDay.getSticker());
    if (pidorSticker.isPresent()) {
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new SendStickerBotAction(chatId, pidorSticker.get().getRandom()),
              botService.canPinMessage(chatId)));
      botActionCollector.wait(chatId, ChatAction.TYPING);
    }
    botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());
  }
}
