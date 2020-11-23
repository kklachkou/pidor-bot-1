package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameResult;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.*;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNameLinkedPidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.DiceService;
import by.kobyzau.tg.bot.pbot.service.ExcludeGameService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GameCommandHandler implements CommandHandler {

  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private PidorService pidorService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private CalendarSchedule calendarSchedule;
  @Autowired private DiceService diceService;
  @Autowired private Logger logger;

  @Autowired private ExcludeGameService excludeGameService;

  @Override
  public void processCommand(Message message, String text) {
    ScheduledItem item = calendarSchedule.getItem(DateUtil.now());
    switch (item) {
      case EMOJI_GAME:
        processEmojiGame(message.getChatId());
        return;
      case EXCLUDE_GAME:
        processExcludeGame(message.getChatId());
        return;
      default:
        botActionCollector.text(
            message.getChatId(), new RandomText("Сегодня не играем", "Сегодня нет игры"));
    }
  }

  private void processExcludeGame(long chatId) {
    Optional<Pidor> pidorOfTheDay =
        dailyPidorRepository
            .getByChatAndDate(chatId, DateUtil.now())
            .map(DailyPidor::getPlayerTgId)
            .map(id -> pidorService.getPidor(chatId, id))
            .filter(Optional::isPresent)
            .map(Optional::get);
    if (pidorOfTheDay.isPresent()) {
      botActionCollector.text(
          chatId,
          new ParametizedText(
              "Сегодня была игра 'Чур не я', но {0} уже проиграл и стал пидором дня",
              new FullNamePidorText(pidorOfTheDay.get())));
      return;
    }
    Set<Integer> playedIds =
        excludeGameService.getExcludeGameUserValues(chatId, DateUtil.now()).stream()
            .map(ExcludeGameUserValue::getPlayerTgId)
            .collect(Collectors.toSet());
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "Сегодня играем в '{0}'. Произнеси это слово первым!",
            new SimpleText(excludeGameService.getWordOfTheDay(DateUtil.now()))));

    List<Pidor> notPlayedPidors =
        pidorService.getByChat(chatId).stream()
            .filter(p -> !playedIds.contains(p.getTgId()))
            .collect(Collectors.toList());
    TextBuilder textBuilder =
        new TextBuilder(new SimpleText("Список людей, кто еще в игре:"))
            .append(new NewLineText())
            .append(new NewLineText());
    notPlayedPidors.stream()
        .map(p -> new TextBuilder(new ShortNameLinkedPidorText(p)).append(new NewLineText()))
        .forEach(textBuilder::append);
    botActionCollector.wait(chatId, 1, ChatAction.TYPING);
    botActionCollector.text(chatId, textBuilder);
  }

  private void processEmojiGame(long chatId) {
    Optional<EmojiGame> game = diceService.getGame(DateUtil.now());
    game.ifPresent(
        emojiGame ->
            botActionCollector.text(
                chatId,
                new ParametizedText(
                    "Сегодня проходит {0}. Кидай {1}",
                    new SimpleText(emojiGame.getType().getGameName()),
                    new SimpleText(emojiGame.getEmoji()))));

    Optional<Pidor> pidorOfTheDay =
        dailyPidorRepository
            .getByChatAndDate(chatId, DateUtil.now())
            .map(DailyPidor::getPlayerTgId)
            .map(id -> pidorService.getPidor(chatId, id))
            .filter(Optional::isPresent)
            .map(Optional::get);
    if (pidorOfTheDay.isPresent()) {
      botActionCollector.text(
          chatId,
          new ParametizedText(
              "Но {0} уже проиграл и стал пидором дня",
              new FullNamePidorText(pidorOfTheDay.get())));
      return;
    }
    printListOfPidors(chatId);
  }

  private void printListOfPidors(long chatId) {

    List<Pidor> pidors = pidorService.getByChat(chatId);
    TextBuilder textBuilder =
        new TextBuilder(new SimpleText("Актуальный список кандидатов на пидора дня:"))
            .append(new NewLineText())
            .append(new NewLineText());
    pidors.stream()
        .map(p -> new Pair<>(p, getUserDice(p)))
        .sorted(Comparator.comparing(Pair::getRight))
        .map(
            p ->
                new TextBuilder()
                    .append(new SimpleText(getSymbol(chatId, p.getRight())))
                    .append(new SimpleText(" "))
                    .append(new ShortNameLinkedPidorText(p.getLeft()))
                    .append(new SimpleText(" - "))
                    .append(new IntText(p.getRight()))
                    .append(new NewLineText()))
        .forEach(textBuilder::append);

    botActionCollector.text(chatId, textBuilder);
  }

  private String getSymbol(long chatId, int userDice) {
    EmojiGame game = diceService.getGame(DateUtil.now()).orElseThrow(IllegalStateException::new);
    String emoji = game.getEmoji();
    EmojiGameResult gameResult = game.getResult(chatId, userDice);
    if (gameResult == EmojiGameResult.NONE) {
      return emoji;
    } else {
      return gameResult.getSymbol();
    }
  }

  private int getUserDice(Pidor pidor) {
    return diceService
        .getUserDice(pidor.getChatId(), pidor.getTgId(), DateUtil.now())
        .map(PidorDice::getValue)
        .orElse(0);
  }

  @Override
  public Command getCommand() {
    return Command.GAME;
  }
}
