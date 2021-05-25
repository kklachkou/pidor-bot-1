package by.kobyzau.tg.bot.pbot.handlers.update.fun;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("PidorOfTheDayIntro")
public class PidorOfTheDayIntro implements Intro {

  @Autowired private BotActionCollector botActionCollector;

  private final Selection<String> firstMessages;
  private final Selection<String> messages;

  public PidorOfTheDayIntro() {
    firstMessages =
        new ConsistentSelection<>(
            "Осторожно! Пидор дня активирован!",
            "Игра начинается",
            "Активация пидорского генератора",
            "Активация пидорских систем",
            "Включение анального компьютера",
            "Разогрев анального криптоанализатора",
            "Зачем вы меня разбудили?",
            "Если ты этого желаешь, то тогда погнали",
            "Наташа, морская пехота, стартуем!",
            "U wanna play? Let's play!",
            "Опять вы в эти игрули играете...",
            "Ну что, начнём игру",
            "Думаю можно начинать",
            "Да что с вами не так?",
            "Итак... кто же сегодня пидор дня?");
    this.messages =
        new ConsistentSelection<>(
            "Военный спутник запущен, коды доступа внутри...",
            "В этом совершенно нет смысла...",
            "Машины выехали",
            "Привлекаю к определению пидора экстрасенсов",
            "Только не он...",
            "Действую па-ашчушчэнiям",
            "Ощущаю чей-то зад",
            "Где-же он...",
            "Уууффь",
            "Передергиваем...",
            "Опрашиваем посетителей Мегацентра...",
            "Произвожу замеры",
            "Настройка датчиков обнаружения пидоров",
            "Настраиваю свои датчики",
            "No no no no",
            "Только не это!",
            "Думаю всё очевидно",
            "Обнаружен запах пидора",
            "Загрузка баз данных латентных пидоров",
            "Пидор ближе чем кажется",
            "Расстановка приоритетов",
            "Взвешиваю туалетную бумагу",
            "Оцениваю ситуацию",
            "Ищись пидор большой и маленький",
            "Пидор, кис кис кис",
            "Пидор, думаешь я тебя не найду?",
            "Поиск...",
            "Сравнение отпечатков задницы",
            "Разогрев анального криптоанализатора...",
            "Расследование завершено",
            "Интересно..",
            "Пидор-патруль активирован",
            "Дайте подумать...",
            "В этом совершенно нет смысла...",
            "Так, что тут у нас?",
            "Машины выехали",
            "Я в опасности, системы повреждены!");
  }

  @Override
  public void sendIntro(long chatId) {
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText(firstMessages.next()));
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
