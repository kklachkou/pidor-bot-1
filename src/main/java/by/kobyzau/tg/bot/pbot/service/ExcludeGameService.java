package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.ExcludeGameUserValue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExcludeGameService {

  boolean isExcludeGameDay(LocalDate localDate);

  Optional<ExcludeGameUserValue> getExcludeGameUserValue(
      long chatId, int userId, LocalDate localDate);

  List<ExcludeGameUserValue> getExcludeGameUserValues(long chatId, LocalDate localDate);

  void saveExcludeGameUserValue(ExcludeGameUserValue userValue);

  String getWordOfTheDay(LocalDate date);

  int getNumPidorsToExclude(long chatId);

  boolean needToFinalize(long chatId);

}
