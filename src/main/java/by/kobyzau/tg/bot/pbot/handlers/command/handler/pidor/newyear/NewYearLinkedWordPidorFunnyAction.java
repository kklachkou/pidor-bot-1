package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.newyear;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.PidorFunnyAction;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.RepeatPidorProcessor;
import by.kobyzau.tg.bot.pbot.handlers.update.fun.Intro;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.PrioritySelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.UserLinkText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendStickerBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Profile("new-year")
@Component
public class NewYearLinkedWordPidorFunnyAction implements PidorFunnyAction {

  @Autowired
  @Qualifier("PidorOfTheDayIntro")
  private Intro intro;

  private final Selection<String> finalMessage;
  private final Selection<String> pidorName;

  @Autowired private RepeatPidorProcessor repeatPidorProcessor;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private BotService botService;

  public NewYearLinkedWordPidorFunnyAction() {
    this.finalMessage =
        new ConsistentSelection<>(
            "Согласно спискам, {0} - ты сегодня пидор дня!",
            "{0} - не нужно быть дедушкой Пидор-Ботом, чтобы догадаться, что ты сегодня пидор дня",
            "Если кто-нибудь отправит мне открытку с вопросом кто сегодня пидор дня, я отвечу {0}",
            "Если кто-нибудь загадает желание сделать кого-нибудь пидором дня, я отвечу {0} - станет пидором дня",
            "И снегурочка этого дня сегодня... а нет, ошибка, всего-лишь пидор - {0}",
            "Ого, вы посмотрите только! А румяный пидор дня то - {0}",
            "А сегодня наш зимний пидор это {0}",
            "Думаешь скрыться от новогоднего чуда? {0} - оно тебя настигнет и сделает пидором дня",
            "Думаю всё очевидно, {0} - ты пидор этого прекрасного зимнего дня!",
            "Мистер пукинштейн, {0} - выглядишь педиковато, ты пидор дня!",
            "Согласно новогодним пожеланиям, {0} сегодня пидор дня",
            "Если не секрет, {0} - скажи, почему ты пидор дня?",
            "Доставайте феерверк, у нас сегодня пидор - {0}",
            "А любитель ронять мандаринки - {0}");
    this.pidorName =
        new ConsistentSelection<>(
            "пьяный эльф",
            "снеговик, с неправильной морковкой",
            "любитель ронять мандаринки при свидетелях Иеговы",
            "снегурка со щетиной и диким перегаром",
            "человек с холодным сердцем и горячим задом",
            "Дедушка Пидор",
            "пешка Санта Клауса",
            "звезда по имени Солнышко");
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
    botActionCollector.sticker(chatId, StickerType.NEW_YEAR.getRandom());
    botActionCollector.wait(chatId, ChatAction.TYPING);

    Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidorOfTheDay.getSticker());
    if (pidorSticker.isPresent()) {
      botActionCollector.text(
          chatId,
          new ParametizedText(
              finalMessage.next(),
              new UserLinkText(pidorOfTheDay.getTgId(), new SimpleText(pidorName.next()))));
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
                  new ParametizedText(
                      finalMessage.next(),
                      new UserLinkText(pidorOfTheDay.getTgId(), new SimpleText(pidorName.next())))),
              botService.canPinMessage(chatId)));
      botActionCollector.wait(chatId, ChatAction.TYPING);
    }
    botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());
    repeatPidorProcessor.checkPidorRepeat(pidorOfTheDay);
  }
}
