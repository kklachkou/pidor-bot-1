package by.kobyzau.tg.bot.pbot.service.pidor;

import by.kobyzau.tg.bot.pbot.model.Pidor;

public interface PidorOfDayService {

  Pidor findPidorOfDay(long chatId);


  Type getType();

  enum Type {
    SIMPLE,
    DICE,
    ELECTION
  }
}
