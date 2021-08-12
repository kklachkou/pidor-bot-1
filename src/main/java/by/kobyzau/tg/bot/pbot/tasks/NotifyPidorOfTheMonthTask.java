package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component("notifyPidorOfTheMonthTask")
public class NotifyPidorOfTheMonthTask implements Task {

  @Autowired private PidorRepository pidorRepository;

  @Autowired private DailyPidorRepository dailyPidorRepository;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private Logger logger;

  @Autowired private BotService botService;

  @Autowired private TelegramService telegramService;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    LocalDate now = DateUtil.now();
    if (now.plusDays(1).getDayOfMonth() != 1) {
      return;
    }
    logger.info("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");

    telegramService.getChatIds()
        .forEach(chatId -> executor.execute(() -> processForChat(chatId)));
  }

  private void processForChat(long chatId) {
    botActionCollector.typing(chatId);
    LocalDate now = DateUtil.now();
    Map<Long, Integer> numWins = new HashMap<>();

    dailyPidorRepository.getByChat(chatId).stream()
        .filter(d -> d.getLocalDate().getYear() == now.getYear())
        .filter(d -> d.getLocalDate().getMonthValue() == now.getMonthValue())
        .forEach(
            d -> numWins.put(d.getPlayerTgId(), numWins.getOrDefault(d.getPlayerTgId(), 0) + 1));
    printWinnerInfo(chatId, numWins, now.getMonthValue());
  }

  private void printWinnerInfo(long chatId, Map<Long, Integer> numWins, int month) {

    Optional<Integer> topNumWins =
        numWins.values().stream()
            .filter(Objects::nonNull)
            .filter(n -> n != 0)
            .max(Integer::compareTo);
    if (!topNumWins.isPresent()) {
      return;
    }

    List<Pidor> pidors =
        numWins.entrySet().stream()
            .filter(e -> e.getValue() != null)
            .filter(e -> e.getValue().equals(topNumWins.get()))
            .map(Map.Entry::getKey)
            .map(id -> pidorRepository.getByChatAndPlayerTgId(chatId, id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

    if (pidors.size() == 1) {
      printAboutPidor(pidors.get(0), topNumWins.get(), month);
    } else if (pidors.size() > 1) {
      printAboutPidors(pidors, topNumWins.get(), month);
    }
  }

  private void printAboutPidor(Pidor pidor, int numWins, int month) {
    long chatId = pidor.getChatId();
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(new SimpleText("Вот и {0} закончился"), new SimpleMonth(month)));
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Время определять Пидора Месяца!"));
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new SimpleText("В прошлом месяце 1 человек был пидором больше всего..."));
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            new SimpleText("Целых {0} дня(ей) за весь {1}"),
            new IntText(numWins),
            new SimpleMonth(month)));
    botActionCollector.wait(chatId, 4, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            new SimpleText("Так что давайте поздравим {0}, теперь он официально Пидор {1}"),
            new FullNamePidorText(pidor),
            new PastMonth(month)));
  }

  private void printAboutPidors(List<Pidor> pidors, int numWins, int month) {
    long chatId = pidors.get(0).getChatId();
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(new SimpleText("Вот и {0} закончился"), new SimpleMonth(month)));
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Время определять Пидора Месяца!"));
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("О нет... В прошлом месяце нету победителя..."));
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.text(chatId, new SimpleText("Потому что их несколько!"));
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            new SimpleText("Целых {0} дня(ей) за весь {1}"),
            new IntText(numWins),
            new SimpleMonth(month)));
    botActionCollector.wait(chatId, 4, ChatAction.TYPING);
    String pidorNames =
        pidors.stream()
            .map(FullNamePidorText::new)
            .map(FullNamePidorText::text)
            .collect(Collectors.joining(", "));
    botActionCollector.text(
        chatId,
        new ParametizedText(
            new SimpleText("Так что давайте поздравим {0}, каждый из них официально Пидор {1}"),
            new SimpleText(pidorNames),
            new PastMonth(month)));
  }
}
