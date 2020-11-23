package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.fun.Intro;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.PrioritySelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendStickerBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CombinedPidorFunnyAction implements PidorFunnyAction {

  @Autowired
  @Qualifier("PidorOfTheDayIntro")
  private Intro intro;

  private final Selection<String> finalMessage;


  @Autowired private RepeatPidorProcessor repeatPidorProcessor;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private BotService botService;

  public CombinedPidorFunnyAction() {
    this.finalMessage =
        new ConsistentSelection<>(

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

    Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidorOfTheDay.getSticker());
    if (pidorSticker.isPresent()) {
      botActionCollector.text(
          chatId, new ParametizedText(finalMessage.next(), new FullNamePidorText(pidorOfTheDay)));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new SendStickerBotAction(chatId, pidorSticker.get().getRandom()),
              botService.canPinMessage(chatId)));
      botActionCollector.wait(chatId, ChatAction.TYPING);
    } else {
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new SendMessageBotAction(
                  chatId,
                  new ParametizedText(finalMessage.next(), new FullNamePidorText(pidorOfTheDay))),
              botService.canPinMessage(chatId)));
      botActionCollector.wait(chatId, ChatAction.TYPING);
    }
    botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());
    repeatPidorProcessor.checkPidorRepeat(pidorOfTheDay);
  }
}
