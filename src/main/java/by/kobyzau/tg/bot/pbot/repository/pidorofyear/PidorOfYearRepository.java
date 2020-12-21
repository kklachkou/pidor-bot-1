package by.kobyzau.tg.bot.pbot.repository.pidorofyear;

import by.kobyzau.tg.bot.pbot.model.PidorOfYear;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;

import java.util.List;

public interface PidorOfYearRepository extends CrudRepository<PidorOfYear> {

    List<PidorOfYear> getPidorOfYearByChat(long chatId);
}
