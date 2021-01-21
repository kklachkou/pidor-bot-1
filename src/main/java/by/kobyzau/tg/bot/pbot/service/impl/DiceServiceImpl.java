package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.PidorDice;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.dice.DiceRepository;
import by.kobyzau.tg.bot.pbot.service.DiceService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DiceServiceImpl implements DiceService {

  @Autowired private DiceRepository diceRepository;
  @Autowired private List<EmojiGame> games;

  @Autowired private PidorService pidorService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private CalendarSchedule calendarSchedule;

  @Override
  public List<PidorDice> getDices() {
    return diceRepository.getAll();
  }

  @Override
  public List<PidorDice> getDices(long chatId, LocalDate date) {
    return diceRepository.getAll().stream()
        .filter(d -> d.getChatId() == chatId)
        .filter(d -> d.getLocalDate().isEqual(date))
        .collect(Collectors.toList());
  }

  @Override
  public void saveDice(PidorDice dice) {
    if (dice.getId() == 0) {
      diceRepository.create(dice);
    } else {
      diceRepository.update(dice);
    }
  }

  @Override
  public Optional<PidorDice> getUserDice(long chatId, int userId, LocalDate date) {
    return diceRepository.getAll().stream()
        .filter(d -> d.getChatId() == chatId)
        .filter(d -> d.getPlayerTgId() == userId)
        .filter(d -> d.getLocalDate().isEqual(date))
        .findFirst();
  }

  @Override
  public Optional<EmojiGame> getGame(long chatId, LocalDate date) {
    if (calendarSchedule.getItem(chatId, date) != ScheduledItem.EMOJI_GAME) {
      return Optional.empty();
    }
    return games.stream().filter(g -> g.isDateToGame(date)).findFirst();
  }

  @Override
  public int getNumPidorsToPlay(long chatId) {
    int numPidors = pidorService.getByChat(chatId).size();
    if (numPidors <= 5) {
      return numPidors;
    }
    return (int) (numPidors * 0.8);
  }

  @Override
  public boolean needToFinalize(long chatId) {
    LocalDate now = DateUtil.now();
    int numPidorsToPlay = getNumPidorsToPlay(chatId);
    long diceNumber =
        getDices().stream()
            .filter(d -> d.getChatId() == chatId)
            .filter(d -> d.getLocalDate().isEqual(now))
            .count();
    return (diceNumber >= numPidorsToPlay)
        && !dailyPidorRepository.getByChatAndDate(chatId, DateUtil.now()).isPresent();
  }
}
