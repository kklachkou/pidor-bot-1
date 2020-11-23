package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased;

import java.time.LocalDate;

public interface OneTimePidorFunnyAction extends DateBasePidorFunnyAction {

  LocalDate getDate();

  default boolean testDate(LocalDate date) {
    return getDate().isEqual(date);
  }
}
