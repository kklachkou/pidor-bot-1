package by.kobyzau.tg.bot.pbot.handlers.update.fun;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("NewYearPidorOfTheDayIntro")
public class NewYearPidorOfTheDayIntro implements Intro {

  @Autowired private BotActionCollector botActionCollector;

  private final Selection<String> firstMessages;
  private final Selection<String> messages;

  public NewYearPidorOfTheDayIntro() {
    firstMessages =
        new ConsistentSelection<>(
            "Хо-хо-хо! Пидор дня активирован!",
            "Хо-хо-хо",
            "Ну что, посмотрим кто сегодня был плохим мальчиком",
            "Активация новогодне-пидорских систем",
            "Зачем вы меня разбудили в этот прекрасный зимний день?",
            "Загадай новогоднее желание, мы начинаем",
            "Хо-хо-хо, с Вами ваш дедушка Пидор-Бот!",
            "Загадывайте желание, Пидор-Бот на связи!",
            "Хо-хо-хо, поищем кто вёл сегодня себя плохо?",
            "Пидорские эльфы шепчут мне, что сейчас мой выход",
            "Итак... кто же сегодня пидор дня?");
    this.messages =
        new ConsistentSelection<>(
            "Сани пидор-бота выехали...",
            "Анализ списка плохих мальчиков...",
            "Хо-хо-хо...",
            "Привлекаю к определению пидора эльфов...",
            "Разогреваю замершие датчики...",
            "Действую па навагоднiм ашчушчэнiям...",
            "Раскручиваю гирлянду...",
            "Обсуждаю планы с эльфами...",
            "Кто тут плохой мальчик?",
            "Опрашиваю снеговиков...",
            "Настраиваю свои новогодние датчики",
            "ХО-ХО-ХО",
            "Ищу пидора чудесным образом...",
            "Опусташаю все красные мешочки...",
            "Вписываю имя в новогоднюю открытку...",
            "Обнаружен запах пидора и мандаринок...",
            "Пидор ближе чем кажется...",
            "Вижу следы задницы на снегу...",
            "Хо-хо-хо, ищись пидор...",
            "Пидор, думаешь дед Пидор-Бот тебя не найдет?",
            "Открываю бутылочку шампанского...",
            "Сравнение отпечатков задницы на снегу...",
            "Разогрев новогоднего криптоанализатора...",
            "Залезаю в чей-то дымоход...",
            "Залезаю в чью-то трубу...",
            "Интересно..",
            "О нет! Это не тот дымоход...",
            "Дайте подумать...",
            "Опрашиваю тех, кто еще не верит в Пидор-Бота...",
            "Я в опасности, системы заморожены!");
  }

  @Override
  public void sendIntro(long chatId) {
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText(firstMessages.next()));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.NEW_YEAR.getRandom());
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText(messages.next()));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText(messages.next()));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText(messages.next()));
    botActionCollector.wait(chatId, 4, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText(messages.next()));
  }
}
