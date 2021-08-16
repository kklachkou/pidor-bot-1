package by.kobyzau.tg.bot.pbot.repository.yearlystat;

import by.kobyzau.tg.bot.pbot.model.PidorYearlyStat;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;
import java.util.List;

public interface PidorYearlyStatRepository extends CrudRepository<PidorYearlyStat> {

  List<PidorYearlyStat> getByChatId(long chatId);
}
