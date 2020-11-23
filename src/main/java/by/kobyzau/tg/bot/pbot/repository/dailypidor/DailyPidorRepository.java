package by.kobyzau.tg.bot.pbot.repository.dailypidor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;

public interface DailyPidorRepository extends CrudRepository<DailyPidor> {

  List<DailyPidor> getByChat(long chatId);

  Optional<DailyPidor> getByChatAndDate(long chatId, LocalDate localDate);
}
