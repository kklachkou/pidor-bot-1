package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased.onetime;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.RepeatPidorProcessor;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased.OneTimePidorFunnyAction;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.PseudoText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendStickerBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CoronaOneTimePidorFunnyAction implements OneTimePidorFunnyAction {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private RepeatPidorProcessor repeatPidorProcessor;

  @Override
  public LocalDate getDate() {
    return LocalDate.of(2021, 9, 3);
  }

  @Override
  public void processFunnyAction(long chatId, Pidor pidorOfTheDay) {
    List<Text> messages =
        Stream.of(
                "О нет!",
                "Я опять подхватил <b>короновирус</b>!",
                "ƸŬº\uD83D\uDE25ń⚄Ɔ☿àpď'ƕł¾fąȮ☈ũď\uD83D\uDE4AƏ¨\uD83D\uDE17♜")
            .map(SimpleText::new)
            .collect(Collectors.toList());

    Stream.of(
            "Перехожу на резервный генератор",
            "Трудно что-то говорить...",
            "Анализирую вирус...",
            "Ищу первоисточник заражения...",
            "О нет! Вирус передается половым путем!")
        .map(PseudoText::new)
        .forEach(messages::add);
    messages.add(
        new PseudoText(
            new ParametizedText(
                "Так что не трогайте сегодня {0}, он - пидор дня",
                new ShortNamePidorText(pidorOfTheDay))));
    for (Text message : messages) {
      botActionCollector.text(chatId, message);
      botActionCollector.wait(chatId, ChatAction.TYPING);
    }
    Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidorOfTheDay.getSticker());
    if (pidorSticker.isPresent()) {
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new SendStickerBotAction(chatId, pidorSticker.get().getRandom())));
      botActionCollector.wait(chatId, ChatAction.TYPING);
    }
    botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.COVID.getRandom());
    repeatPidorProcessor.checkPidorRepeat(pidorOfTheDay);
  }
}
