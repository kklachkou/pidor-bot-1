package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.fun.Intro;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.PrioritySelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.UserLinkText;
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

@Profile("!new-year")
@Component
public class LinkedWordPidorFunnyAction implements PidorFunnyAction {

  @Autowired
  @Qualifier("PidorOfTheDayIntro")
  private Intro intro;

  private final Selection<String> finalMessage;
  private final Selection<String> pidorName;

  @Autowired private RepeatPidorProcessor repeatPidorProcessor;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private BotService botService;
  @Autowired private FeedbackService feedbackService;

  public LinkedWordPidorFunnyAction() {
    this.finalMessage =
        new ConsistentSelection<>(
            "Кажется, пидор дня - {0}",
            "Думаю, что {0} пидор дня",
            "Зачем что-то утаивать, ведь {0} пидор дня",
            "{0} - не нужно быть пидор-ботом, чтобы догадаться, что ты сегодня пидор дня",
            "Если кто-то спросит меня, кто сегодня пидор дня, я отвечу {0}",
            "И прекрасный человек дня сегодня... а нет, ошибка, всего-лишь пидор - {0}",
            "Ого, вы посмотрите только! А пидор дня то - {0}",
            "А сегодня наш пидор это {0}",
            "Думаешь скрыться от судьбы? {0} - она тебя настигнет и сделает пидором дня",
            "Думаю всё очевидно, {0} - ты пидор дня!",
            "Лол, кек, чебурек, а {0} - пидор дня!",
            "Согласно гороскопу, {0} сегодня пидор дня",
            "Если не секрет, {0} - скажи, почему ты пидор дня?",
            "Ну ты и пидор, {0}",
            "А любитель ронять мыло - {0}",
            "Думаю пидор сегодняшнего дня - {0}");
    this.pidorName =
        new ConsistentSelection<>(
            "крэпкий хозяйственник из элитного колхоза",
            "крепкий орешек с автоматом без магазина",
            "русский кукловод из украинской Чехии",
            "любитель ронять мыло при свидетелях Иеговы",
            "легитимный заднеприводный проститут",
            "звезда вульгарного кино",
            "пешка Навального",
            "звезда по имени Солнышко",
            "наркоман, алкоголик, проститутка, продавший страну за 20 евро");
  }

  @Override
  public PrioritySelection.Priority getPriority() {
    return PrioritySelection.Priority.LOW;
  }

  @Override
  public void processFunnyAction(long chatId, Pidor pidorOfTheDay) {

    botActionCollector.sticker(chatId, StickerType.LOOKING_PIDOR.getRandom());
    intro.sendIntro(chatId);
    botActionCollector.wait(chatId, ChatAction.TYPING);

    Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidorOfTheDay.getSticker());
    if (pidorSticker.isPresent()) {
      botActionCollector.add(
          new ReplyKeyboardBotAction(
              chatId,
              new ParametizedText(
                  finalMessage.next(),
                  new UserLinkText(pidorOfTheDay.getTgId(), new SimpleText(pidorName.next()))),
              InlineKeyboardMarkup.builder()
                  .keyboardRow(feedbackService.getButtons(FeedbackType.PIDOR))
                  .build(),
              null));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new SendStickerBotAction(chatId, pidorSticker.get().getRandom()),
              botService.canPinMessage(chatId)));
      botActionCollector.wait(chatId, ChatAction.TYPING);
    } else {
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new ReplyKeyboardBotAction(
                  chatId,
                  new ParametizedText(
                      finalMessage.next(),
                      new UserLinkText(pidorOfTheDay.getTgId(), new SimpleText(pidorName.next()))),
                  InlineKeyboardMarkup.builder()
                      .keyboardRow(feedbackService.getButtons(FeedbackType.PIDOR))
                      .build(),
                  null),
              botService.canPinMessage(chatId)));
      botActionCollector.wait(chatId, ChatAction.TYPING);
    }
    botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());
    repeatPidorProcessor.checkPidorRepeat(pidorOfTheDay);
  }
}
