package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.ParsedCommand;
import by.kobyzau.tg.bot.pbot.handlers.command.parser.CommandParser;
import by.kobyzau.tg.bot.pbot.handlers.update.fun.Intro;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.PollService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.PollBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.PollBotActionBuilder;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler.POLL_ORDER;

@Component
@Order(POLL_ORDER)
public class PollPidorUpdateHandler implements UpdateHandler {

  @Autowired private PollService pollService;

  private final Selection<String> finalMessage;
  private final Selection<String> questions;
  private final Selection<String> repeat;

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private CommandParser commandParser;
  @Autowired private Intro intro;
  @Autowired private PidorService pidorService;
  @Autowired private BotService botService;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @Autowired private DailyPidorRepository dailyPidorRepository;

  public PollPidorUpdateHandler() {
    this.finalMessage =
        new ConsistentSelection<>(
            Arrays.asList(
                "Пидор дня найдет, но попробуй найти его",
                "Ответ неясен...пока",
                "Угадайте, кто сегодня пидор дня",
                "Ищите, ищите, ищите пидора!",
                "Догадайся сам кто сегодня пидор",
                "Думаешь я тебе так просто отвечу?"));
    this.questions =
        new ConsistentSelection<>(
            "Как думаешь, кто пидор дня?",
            "Попробуй угадать пидора дня",
            "Догадываешься кто сегодня пидор дня?");

    this.repeat = new ConsistentSelection<>("Догадайся сам", "Ищи в опросе", "Я не скажу");
  }

  @Override
  public boolean test(LocalDate date) {
    return pollService.isDateToPoll(date);
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
    Optional<DailyPidor> dailyPidor = dailyPidorRepository.getByChatAndDate(chatId, DateUtil.now());
    if (dailyPidor.isPresent()) {
      botActionCollector.text(chatId, new SimpleText(repeat.next()));
      return true;
    }
    List<Pidor> pidors = pidorService.getByChat(chatId);
    Pidor pidorOfTheDay = CollectionUtil.getRandomValue(pidors);
    saveDailyPidor(pidorOfTheDay, message.getFrom().getId());
    executor.execute(() -> findPidorOfTheDay(chatId, pidorOfTheDay));
    return true;
  }

  private void findPidorOfTheDay(long chatId, Pidor pidorOfTheDay) {
    intro.sendIntro(chatId);
    sendPoll(chatId, pidorOfTheDay);
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

  private void sendPoll(long chatId, Pidor pidorOfTheDay) {
    List<String> pidors =
        pidorService.getByChat(chatId).stream()
            .filter(p -> p.getTgId() != pidorOfTheDay.getTgId())
            .map(ShortNamePidorText::new)
            .map(Text::text)
            .limit(9)
            .collect(Collectors.toList());
    Text pidorOfTheDayName = new ShortNamePidorText(pidorOfTheDay);
    pidors.add(pidorOfTheDayName.text());
    if (pidors.size() == 1) {
      pidors.add("Губка боб");
    }
    pidors = CollectionUtil.getRandomList(pidors);
    int position = pidors.indexOf(pidorOfTheDayName.text());
    PollBotAction pollBotAction =
        PollBotActionBuilder.forChat(chatId)
            .withQuestion(questions.next())
            .withOptions(pidors)
            .isAnonymous(false)
            .withType(PollBotActionBuilder.Type.QUIZ)
            .withCorrectAnswer(position)
            .build();

    botActionCollector.text(chatId, new SimpleText(finalMessage.next()));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.add(
        new PingMessageWrapperBotAction(pollBotAction, botService.canPinMessage(chatId)));
  }
}
