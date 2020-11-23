package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameResult;
import by.kobyzau.tg.bot.pbot.bots.game.dice.DiceFinalizer;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.ParsedCommand;
import by.kobyzau.tg.bot.pbot.handlers.command.parser.CommandParser;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorDice;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.DiceService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Dice;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler.DICE_ORDER;

@Component
@Order(DICE_ORDER)
public class DiceDayUpdateHandler implements UpdateHandler {

  @Autowired private DiceService diceService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private CommandParser commandParser;
  @Autowired private PidorService pidorService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private DiceFinalizer diceFinalizer;
  @Autowired private BotService botService;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @Override
  public boolean test(LocalDate date) {
    return diceService.getGame(date).isPresent();
  }

  @Override
  public boolean handleUpdate(Update update) {
    if (!validateUpdate(update)) {
      return false;
    }
    boolean hasPidorOfTheDay = hasPidorOfTheDay(update.getMessage().getChatId());
    Optional<Message> message = getMessageWithDice(update);
    if (!message.isPresent()) {
      Optional<Command> command = getCommand(update);
      if (command.isPresent() && command.get() == Command.PIDOR && !hasPidorOfTheDay) {
        botActionCollector.text(
            update.getMessage().getChatId(), new SimpleText("Сегодня проходит игра /game"));
        return true;
      }
      return false;
    }
    if (hasPidorOfTheDay) {
      return false;
    }
    LocalDate now = DateUtil.now();
    EmojiGame game = diceService.getGame(now).orElseThrow(IllegalStateException::new);
    if (!isUserSendValidEmoji(game, message.get())) {
      return true;
    }
    long chatId = message.get().getChatId();

    Optional<PidorDice> userDice =
        diceService.getUserDice(chatId, message.get().getFrom().getId(), now);
    if (userDice.isPresent()) {
      return true;
    }
    botActionCollector.wait(chatId, ChatAction.TYPING);
    int newDiceUserValue = message.get().getDice().getValue();
    diceService.saveDice(
        new PidorDice(message.get().getFrom().getId(), chatId, now, newDiceUserValue));
    if (newDiceUserValue == 1) {
      botActionCollector.text(
          chatId,
          new RandomText("Окей, твоё очко задействовано!", "Я тебя понял!", "Спасибо!"),
          message.get().getMessageId());
      botActionCollector.sticker(chatId, StickerType.LOL.getRandom());
    } else {
      botActionCollector.text(
          chatId,
          new RandomText("Окей, твоё очко задействовано!", "Я тебя понял!", "Спасибо!"),
          message.get().getMessageId());
    }

    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    printListOfPidors(chatId, game);

    if (needToFinalize(chatId)) {
      executor.execute(() -> diceFinalizer.finalize(chatId));
    }
    return true;
  }

  private boolean isUserSendValidEmoji(EmojiGame game, Message message) {
    String emoji =
        Optional.ofNullable(message).map(Message::getDice).map(Dice::getEmoji).orElse("");
    return emoji.equals(game.getEmoji());
  }

  private void printListOfPidors(long chatId, EmojiGame game) {

    List<Pidor> pidors = pidorService.getByChat(chatId);
    TextBuilder textBuilder =
        new TextBuilder(new SimpleText("Актуальный список кандидатов на пидора дня:"))
            .append(new NewLineText())
            .append(new NewLineText());
    for (Pidor pidor : pidors) {
      int userValue = getUserValue(pidor);
      EmojiGameResult result = game.getResult(chatId, userValue);
      switch (result) {
        case NONE:
          textBuilder
              .append(new SimpleText(game.getEmoji()))
              .append(new SimpleText(" "))
              .append(new ShortNamePidorText(pidor))
              .append(new NewLineText());
          break;
        case WIN:
          break;
        case LOSE:
        case DRAW:
          textBuilder
              .append(new SimpleText(result.getSymbol()))
              .append(new SimpleText(" "))
              .append(new ShortNamePidorText(pidor))
              .append(new NewLineText());
          break;
      }
    }
    botActionCollector.text(chatId, textBuilder);
  }

  private int getUserValue(Pidor pidor) {
    return diceService
        .getUserDice(pidor.getChatId(), pidor.getTgId(), DateUtil.now())
        .map(PidorDice::getValue)
        .orElse(0);
  }

  private Optional<Command> getCommand(Update update) {
    if (!update.hasMessage()) {
      return Optional.empty();
    }
    Message message = update.getMessage();
    if (message == null) {
      return Optional.empty();
    }
    if (!message.hasText()) {
      return Optional.empty();
    }
    ParsedCommand parsedCommand = commandParser.parseCommand(message.getText());
    return Optional.ofNullable(parsedCommand).map(ParsedCommand::getCommand);
  }

  private Optional<Message> getMessageWithDice(Update update) {
    if (!update.hasMessage()) {
      return Optional.empty();
    }
    Message message = update.getMessage();

    return Optional.ofNullable(message)
        .filter(m -> m.getDice() != null)
        .filter(m -> m.getDice() != null)
        .filter(m -> m.getDice().getValue() != null)
        .filter(m -> m.getFrom() != null);
  }

  private boolean needToFinalize(long chatId) {
    LocalDate now = DateUtil.now();
    long pidorNumber = pidorService.getByChat(chatId).size();
    int numPidorsToPlay = diceService.getNumPidorsToPlay(chatId);
    long diceNumber =
        diceService.getDices().stream()
            .filter(d -> d.getChatId() == chatId)
            .filter(d -> d.getLocalDate().isEqual(now))
            .count();
    return (pidorNumber == diceNumber || diceNumber >= numPidorsToPlay)
        && !hasPidorOfTheDay(chatId);
  }

  private boolean hasPidorOfTheDay(long chatId) {
    return dailyPidorRepository.getByChatAndDate(chatId, DateUtil.now()).isPresent();
  }

  private boolean validateUpdate(Update update) {
    if (!update.hasMessage()) {
      return false;
    }
    Message message = update.getMessage();
    return botService.isChatValid(message.getChatId())
        && pidorService.getPidor(message.getChatId(), message.getFrom().getId()).isPresent();
  }
}
