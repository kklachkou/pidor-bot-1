package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.ParsedCommand;
import by.kobyzau.tg.bot.pbot.handlers.command.parser.CommandParser;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageWrapper;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;

import static by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler.EDIT_TEXT_ORDER;

@Component
@Order(EDIT_TEXT_ORDER)
public class EditPidorMessageUpdateHandler implements UpdateHandler {

  @Autowired private CalendarSchedule calendarSchedule;

  @Autowired private CommandParser commandParser;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private PidorService pidorService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private BotService botService;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  private final Map<Long, LocalDateTime> callsToChats = new HashMap<>();
  private final Selection<String> firstMessages;
  private final Selection<String> messages;
  private final Selection<String> lastMessages;

  public EditPidorMessageUpdateHandler() {
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
    this.lastMessages =
        new ConsistentSelection<>(
            "А ты точно был внимательным?",
            "Пидора я показал, надеюсь ты его увидел",
            "Если ты еще не знаешь кто сегодня пидор, значит ты был недостаточно внимательным",
            "Будь повнимательнее",
            "Увидел кто пидор?");
  }

  @Override
  public boolean test(LocalDate localDate) {
    return calendarSchedule.getItem(localDate) == ScheduledItem.EDITED_MESSAGE;
  }

  @Override
  public boolean handleUpdate(Update update) {
    if (!validateMessage(update)) {
      return false;
    }
    Message message = update.getMessage();
    long chatId = message.getChatId();
    ParsedCommand parsedCommand = commandParser.parseCommand(message.getText());
    Command command = parsedCommand.getCommand();
    if (command != Command.PIDOR) {
      return false;
    }
    botActionCollector.typing(chatId);
    Pidor pidorOfTheDay = getPidorOfTheDay(chatId, message.getFrom().getId());
    executor.execute(() -> displayPidorOfTheDayInMessage(pidorOfTheDay));
    return true;
  }

  private Pidor getPidorOfTheDay(long chatId, int callerId) {
    Optional<DailyPidor> dailyPidor = dailyPidorRepository.getByChatAndDate(chatId, DateUtil.now());
    if (dailyPidor.isPresent()) {
      return pidorService
          .getPidor(chatId, dailyPidor.get().getPlayerTgId())
          .orElse(Pidor.unknown(chatId));
    }
    Pidor pidorOfTheDay = CollectionUtil.getRandomValue(pidorService.getByChat(chatId));
    saveDailyPidor(pidorOfTheDay, callerId);
    return pidorOfTheDay;
  }

  private void displayPidorOfTheDayInMessage(Pidor pidorOfTheDay) {
    long chatId = pidorOfTheDay.getChatId();
    String firstMessage = firstMessages.next();
    botActionCollector.add(
        new EditMessageWrapper(
            new SendMessageBotAction(chatId, firstMessage),
            botActionCollector,
            executor,
            Arrays.asList(
                new EditMessageWrapper.EditMessageChain(new SimpleText("Готов?"), 7),
                new EditMessageWrapper.EditMessageChain(
                    new ParametizedText("{0} - пидор", new ShortNamePidorText(pidorOfTheDay)), 0.5),
                new EditMessageWrapper.EditMessageChain(new SimpleText(firstMessage), 1))));
    delay(chatId);
    botActionCollector.text(chatId, new SimpleText(messages.next()));
    delay(chatId);
    botActionCollector.text(chatId, new SimpleText(messages.next()));
    delay(chatId);
    botActionCollector.text(chatId, new SimpleText(messages.next()));
    delay(chatId);
    botActionCollector.text(chatId, new SimpleText(messages.next()));
    delay(chatId);
    botActionCollector.text(chatId, new SimpleText(messages.next()));
    delay(chatId);
    botActionCollector.text(chatId, new SimpleText(lastMessages.next()));
    delay(chatId);
    botActionCollector.sticker(chatId, StickerType.PIDOR.getRandom());
  }

  private void delay(long chatId) {
    botActionCollector.typing(chatId);
    try {
      Thread.sleep(CollectionUtil.getRandomValue(Arrays.asList(2, 3, 4, 5)) * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private boolean validateMessage(Update update) {
    if (!update.hasMessage()) {
      return false;
    }
    Message message = update.getMessage();

    if (message.getChatId() == null) {
      return false;
    }
    if (!botService.isChatValid(message.getChatId())) {
      return false;
    }
    if (!message.hasText()) {
      return false;
    }
    User from = message.getFrom();
    if (from == null) {
      return false;
    }
    return from.getId() != null;
  }

  private void saveDailyPidor(Pidor pidor, int callerId) {
    DailyPidor dailyPidor = new DailyPidor();
    dailyPidor.setChatId(pidor.getChatId());
    dailyPidor.setCaller(callerId);
    dailyPidor.setPlayerTgId(pidor.getTgId());
    dailyPidor.setLocalDate(DateUtil.now());
    dailyPidorRepository.create(dailyPidor);
  }
}
