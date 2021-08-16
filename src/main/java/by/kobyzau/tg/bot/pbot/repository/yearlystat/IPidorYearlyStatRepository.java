package by.kobyzau.tg.bot.pbot.repository.yearlystat;

import by.kobyzau.tg.bot.pbot.model.PidorYearlyStat;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface IPidorYearlyStatRepository extends CrudRepository<PidorYearlyStat, Long> {

  @Query("SELECT p FROM PidorYearlyStat p WHERE p.chatId = ?1")
  List<PidorYearlyStat> findByChatId(long chatId);
}
