package by.kobyzau.tg.bot.pbot.bots.game.dice;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameResult;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.PidorFunnyAction;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorDice;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.DiceService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DiceFinalizerImpl implements DiceFinalizer {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private DiceService diceService;

  @Autowired private TelegramService telegramService;

  @Autowired private PidorService pidorService;

  @Autowired private DailyPidorRepository dailyPidorRepository;

  @Autowired private List<PidorFunnyAction> pidorFunnyActions;
  @Autowired private BotService botService;

  @Override
  public void finalize(long chatId) {
    LocalDate now = DateUtil.now();
    if (dailyPidorRepository.getByChatAndDate(chatId, now).isPresent()) {
      return;
    }
    EmojiGame game =
        diceService
            .getGame(now)
            .orElseThrow(() -> new IllegalStateException("Finalizing dice for non dice day"));
    botActionCollector.wait(chatId, 4, ChatAction.TYPING);
    List<Pidor> pidors = pidorService.getByChat(chatId);
    if (CollectionUtil.isEmpty(pidors)) {
      return;
    }
    List<Pidor> pidorsToPlay = new ArrayList<>();
    for (Pidor pidor : pidors) {
      int userDice = getUserDice(pidor);
      EmojiGameResult gameResult = game.getResult(chatId, userDice);
      switch (gameResult) {
        case LOSE:
        case DRAW:
          pidorsToPlay.add(pidor);
          break;
        case NONE:
          pidorsToPlay.add(pidor);
          pidorsToPlay.add(pidor);
          break;
      }
    }
    botActionCollector.text(chatId, new SimpleText("Время подводить итоги!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    if (pidorsToPlay.isEmpty()) {
      botActionCollector.text(
          chatId,
          new SimpleText("Сегодня нет проигравших, так что в пидор-рулетке участвуют все!"));
      pidorsToPlay.addAll(pidors);
    }
    Pidor pidorOfTheDay = CollectionUtil.getRandomValue(pidorsToPlay);
    saveDailyPidor(pidorOfTheDay, pidorOfTheDay.getTgId());
    TextBuilder listOfPidorsText =
        new TextBuilder()
            .append(new SimpleText("Вот список кандидатов на пидора дня:"))
            .append(new NewLineText())
            .append(new NewLineText());
    pidorsToPlay.stream()
        .distinct()
        .map(ShortNamePidorText::new)
        .forEach(p -> listOfPidorsText.append(p).append(new NewLineText()));
    botActionCollector.text(chatId, listOfPidorsText);
    botActionCollector.wait(chatId, 5, ChatAction.TYPING);
    botService.unpinLastBotMessage(chatId);
    CollectionUtil.getRandomValue(pidorFunnyActions).processFunnyAction(chatId, pidorOfTheDay);
  }

  private int getUserDice(Pidor pidor) {
    return diceService
        .getUserDice(pidor.getChatId(), pidor.getTgId(), DateUtil.now())
        .map(PidorDice::getValue)
        .orElse(0);
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
