package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.PidorYearlyStat;
import java.util.Optional;

public interface PidorYearlyStatService {

  Optional<PidorYearlyStat> getStat(long chatId, long userId, int year);

  void createYearlyStat(long chatId, int year);
}
