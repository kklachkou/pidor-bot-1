package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.model.PidorDice;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiceService {

  List<PidorDice> getDices();

  List<PidorDice> getDices(long chatId, LocalDate date);

  void saveDice(PidorDice dice);

  Optional<PidorDice> getUserDice(long chatId, int userId, LocalDate date);

  Optional<EmojiGame> getGame(long chatId, LocalDate localDate);


  int getNumPidorsToPlay(long chatId);

  boolean needToFinalize(long chatId);
}
