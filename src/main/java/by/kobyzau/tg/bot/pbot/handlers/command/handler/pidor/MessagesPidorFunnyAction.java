package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.digest.StringListDigestCalc;
import by.kobyzau.tg.bot.pbot.program.selection.HalfSelection;
import by.kobyzau.tg.bot.pbot.program.selection.PrioritySelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.FeedbackService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.ReplyKeyboardBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendStickerBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Profile("!new-year")
@Component
public class MessagesPidorFunnyAction implements PidorFunnyAction {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private BotService botService;
  @Autowired private RepeatPidorProcessor repeatPidorProcessor;
  @Autowired private FeedbackService feedbackService;

  private List<List<String>> getMessages() {
    return Arrays.asList(
        Arrays.asList(
            "Вышел пидор на крыльцо",
            "Почесать своё яйцо",
            "Сунет руку - нет яица",
            "Пизданулся прям с крыльца",
            "Побежал он быстро в лес",
            "И на дерево залез",
            "Вот ты спросишь - нах*я?",
            "Ответ прост, {0} - ты пидор дня"),
        Arrays.asList(
            "Что-то тут не так...",
            "Вы это чуете?",
            "О да. Это точно он",
            "Тут пахнет пидором...",
            "Oh my God! Holly shit!",
            "И зуб даю это от {0}"),
        Arrays.asList(
            "Вот что вы делаете?",
            "Я вам уже говорил что такое безумие?",
            "Безумие - это повторение одного и того же действия",
            "Раз за разом",
            "В надежде на изменение",
            "ЭТО ЕСТЬ БЕЗУМИЕ",
            "Смысл в том, что это правда",
            "И это везде, это повсюду",
            "Снова, снова и снова",
            "Но иначе не будет",
            "Ведь, {0}, смысл в том что я ёбнутый, а ты - пидор дня"),
        Arrays.asList(
            "- Хьюстон, все системы проверены, шатл готов к полёту к системе Anus-b",
            "- Понял вас, Пидор-1, даю обратный отсчет:",
            "3...",
            "2...",
            "1...",
            "...",
            "- Хьюстон, чувствую тягу в заднем двигателе",
            "- Пидор-1, какое состояние ротового насоса? Приём",
            "- Хьюстон, насос отстасывает биоорганическое топливо в штатном режиме",
            "- Понял вас, Пидор-1, помните - вы делаете важное дело",
            "Весь мир следит за вами",
            "- Спасибо Хсьюстон. Это один маленький шаг для пидораса и огромный скачок для человечества.",
            "- Пидор-1, Иисус с вами",
            "- Хьюстон, Иссус нам не нужен с таким капитаном!",
            " - Вас понял. Капитан {0} - вы наш герой, вы первопроходец, вы наш избранный, вы - пидор дня"),
        Arrays.asList(
            "Внимание, внимание...",
            "Говорит Москва!",
            "Передаем важное правительственное сообщение:",
            "Граждане и гражданки Советского Союза...",
            "Сегодня в 4 часа утра без всякого объявления войны пидорские силы атаковали границы мужского ануса...",
            "Наше дело правое!",
            "Враг будет разбит!",
            "Победа будет за нами!",
            "А {0} будет наказан в соответсвии с законом"),
        Arrays.asList(
            "По секрету, любого на выбор,",
            "расспроси и получишь ответ,",
            "что повсюду – на пидоре пидор!",
            "Ладно б пользы, житья от них нет!",
            "Оттого, наш маршрут – мимо кассы,",
            "пидарасом быть полный залёт.",
            "Охренели вконец, пидорасы,",
            "и вот-вот {0} запоёт!"),
        Arrays.asList(
            "Надоели!",
            "Каждый день одно и тоже",
            "Хоть бы денёк провели как нормальные пацаны",
            "Пиво выпили...",
            "В покер поиграли...",
            "Девчонок полапали..",
            "Тфу на вас!",
            "И дважды на {0} - ты пидор дня"));
  }

  private Selection<List<String>> getFunnyMessages() {
    return new HalfSelection<>(getMessages());
  }

  @Override
  public PrioritySelection.Priority getPriority() {
    return PrioritySelection.Priority.HIGH;
  }

  @Override
  public void processFunnyAction(long chatId, Pidor pidorOfTheDay) {
    Selection<List<String>> funnyMessages = getFunnyMessages();
    List<String> funnyMessageList = funnyMessages.next();

    int size = funnyMessageList.size();
    for (int i = 0; i < size; i++) {
      botActionCollector.wait(chatId, ChatAction.TYPING);
      if (i == (size - 1)) {
        botActionCollector.wait(chatId, ChatAction.TYPING);

        Optional<StickerType> pidorSticker =
            StickerType.getPidorSticker(pidorOfTheDay.getSticker());
        if (pidorSticker.isPresent()) {
          botActionCollector.add(
              new ReplyKeyboardBotAction(
                  chatId,
                  new ParametizedText(
                      new SimpleText(funnyMessageList.get(i)),
                      new FullNamePidorText(pidorOfTheDay)),
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
                          new SimpleText(funnyMessageList.get(i)),
                          new FullNamePidorText(pidorOfTheDay)),
                      InlineKeyboardMarkup.builder()
                          .keyboardRow(feedbackService.getButtons(FeedbackType.PIDOR))
                          .build(),
                      null),
                  botService.canPinMessage(chatId)));
          botActionCollector.wait(chatId, ChatAction.TYPING);
        }
        botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());

      } else {
        botActionCollector.text(chatId, new SimpleText(funnyMessageList.get(i)));
      }
    }
    repeatPidorProcessor.checkPidorRepeat(pidorOfTheDay);
  }
}
