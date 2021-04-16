package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.newyear;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.PidorFunnyAction;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.RepeatPidorProcessor;
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

@Profile("new-year")
@Component
public class NewYearMessagesPidorFunnyAction implements PidorFunnyAction {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private BotService botService;
  @Autowired private RepeatPidorProcessor repeatPidorProcessor;
  @Autowired private FeedbackService feedbackService;

  private List<List<String>> getMessages() {
    return Arrays.asList(
        Arrays.asList(
            "Давайте все вместе позавём дедушку Пидор-Бота!",
            "Кричим! Де-душ-ка Пи-дор Бот!",
            "Хмм, не слышит вас дедушка, видать совсем со старости плохо у него со слухом",
            "Давайте ещё раз",
            "Де-душ-ка Пи-дор Бот!",
            "Опять не появился?",
            "Может дедушки Пидор-Бота не существует?",
            "Давайте тогда спросим {0} - он пидор дня и может нам много чего рассказать о том, кто такой Дед Пидор-Бот"),
        Arrays.asList(
            "Олени вдруг забастовали",
            "Везти Бота отказались!",
            "Эльфы все сбежали в лес",
            "Снеговик на ель залез",
            "Пришлось Боту мозги напрячь,",
            "И {0} запрячь!"),
        Arrays.asList(
            "Опять весёлый наш народ",
            "Встречает Старый Новый год!",
            "А за окном стоит мороз,",
            "Под окном - пьяный Бот Мороз!",
            "Он ещё на Новый год",
            "Перепил честной народ!",
            "Теперь он тяжело вздыхает,",
            "Лежит в снегу и отдыхает!",
            "Даю точный Вам вопрос:",
            "{0} - ты пидор, куда Деда ты понёс?"),
        Arrays.asList(
            "В эту ночь под Новый год",
            "Снег на улице метёт,",
            "А в натопленной избе",
            "Гулянье шумное идёт:",
            "Пидор-бот свечи зажигает,",
            "Снегурка танец исполняет,",
            "Все олени водку пьют,",
            "А {0} - пидор. Салют!"),
        Arrays.asList(
            "В красной шубе,с красным носом,",
            "Бот фигачит по морозу.",
            "В шапке, с палкой и мешком,",
            "И с бухим Снеговиком.",
            "Рядом Кролик в каблуках",
            "И Снегурка на рогах...",
            "Если встретишь этот сброд,",
            "Рядом пидор {0}"),
        Arrays.asList(
            "Удача задом повернулась?",
            "Воспользоваться ей спеши –",
            "Коль в пошлой позе изогнулась,",
            "Дай этой детке от души!",
            "Чтоб в Новый год она скорее",
            "Спешила все тебе отдать!",
            "Ну а когда любовь не греет –",
            "Пидор {0} тебя согреет"),
        Arrays.asList(
            "Дед Мороз пришел к нам в дом",
            "Без подарков и поддатый",
            "Метра два наверно в нем",
            "Борода с тампонной ваты",
            "Водка плещется в глазах",
            "Посох что то вроде х*ра",
            "Неприлично виден пах",
            "На руке тату: {0}"),
        Arrays.asList(
            "Он в пальто коротком слишком",
            "В красных пидорских штанишках",
            "И в ботинках женских - красных,",
            "С эльфами, как гомик грязный!",
            "Старый, низкий, толстый очень",
            "И в очках он - ну короче,",
            "Вы наверно догадались,",
            "Это, дети, {0}!"));
  }

  private Selection<List<String>> getFunnyMessages() {
    return new HalfSelection<>(getMessages());
  }

  @Override
  public PrioritySelection.Priority getPriority() {
    return PrioritySelection.Priority.MEDIUM;
  }

  @Override
  public void processFunnyAction(long chatId, Pidor pidorOfTheDay) {
    botActionCollector.sticker(chatId, StickerType.NEW_YEAR.getRandom());
    botActionCollector.typing(chatId);
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
        botActionCollector.sticker(chatId, StickerType.NEW_YEAR.getRandom());
        botActionCollector.wait(chatId, ChatAction.TYPING);
        botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());

      } else {
        botActionCollector.text(chatId, new SimpleText(funnyMessageList.get(i)));
      }
    }
    repeatPidorProcessor.checkPidorRepeat(pidorOfTheDay);
  }
}
