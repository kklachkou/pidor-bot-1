package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased.onetime;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased.OneTimePidorFunnyAction;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.BoldText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendStickerBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class SherlokOneTimePidorFunnyAction implements OneTimePidorFunnyAction {

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public LocalDate getDate() {
    return LocalDate.of(2021, 5, 26);
  }

  @Override
  public void processFunnyAction(long chatId, Pidor pidorOfTheDay) {
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0}:\nВнимание! Обнаружено место преступления!\nСамоизносилование. Необходимы агенты у голубого переулка",
            new BoldText("Диспейтчер")));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0}:\nДиспейтчер, говорит Ник, отдел кукловодов, не могу прибыть, нахожусь в Польше, наряжаю куклы",
            new BoldText("Nick")));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0}:\nДиспейтчер, говорит Майк, IT-отдел, также не доступен, продолжаю DDoS атаки из Чехии",
            new BoldText("Mike")));

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0}:\nПонял вас, буду искать других агентов", new BoldText("Диспейтчер")));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0}:\nГоворит {1}, отдел убойного пидор-патруля, я прибыл на место",
            new BoldText(new ShortNamePidorText(pidorOfTheDay)),
            new FullNamePidorText(pidorOfTheDay)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0}:\nВижу улики, на полу лежит фалоимитатор зеленого цвета. На нём есть кровь. Много пиратской атрибутики. Также в углу что-то вроде трона, собранного из коробки от холодильника.",
            new BoldText(new ShortNamePidorText(pidorOfTheDay))));

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.LOOKING_PIDOR.getRandom());
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0}:\nДиспейтчер, картина мне ясна. "
                + "Судя по обстановке, тут живёт пенсионер, фанатеющий от пиратов, который упал с коробок на резиновый фалос. "
                + "Больше пострадавших нет, только вижу напуганного Шпица. "
                + "Зайду к соседям, может он сбежал к кому из соседней квартиры. Приём",
            new BoldText(new ShortNamePidorText(pidorOfTheDay))));
    botActionCollector.wait(chatId, 7, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0}:\nОтличная работа! Можете возвращаться в отдел убойного пидор-патруля. С меня сегодня бутылочка Массандры",
            new BoldText("Диспейтчер")));
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "{0}:\nПонял вас. Возвращаюсь на дежурство!",
            new BoldText(new ShortNamePidorText(pidorOfTheDay))));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidorOfTheDay.getSticker());
    if (pidorSticker.isPresent()) {
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new SendStickerBotAction(chatId, pidorSticker.get().getRandom())));
    } else {
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new SendMessageBotAction(
                  chatId,
                  new ParametizedText(
                      "После этого дела {0} стал лучшим агентом пидор-патруля",
                      new FullNamePidorText(pidorOfTheDay)))));
      botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());
    }
  }
}
