package by.kobyzau.tg.bot.pbot.repository.potato;

import by.kobyzau.tg.bot.pbot.model.HotPotatoTaker;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;
import java.time.LocalDate;
import java.util.List;

public interface PotatoTakerRepository extends CrudRepository<HotPotatoTaker> {
  List<HotPotatoTaker> getByChatAndDate(long chatId, LocalDate date);
}
