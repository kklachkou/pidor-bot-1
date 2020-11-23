package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.datebased;

import by.kobyzau.tg.bot.pbot.model.Pidor;

import java.time.LocalDate;

public interface DateBasePidorFunnyAction {

  void processFunnyAction(long chatId, Pidor pidorOfTheDay);

  boolean testDate(LocalDate date);
}
