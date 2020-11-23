package by.kobyzau.tg.bot.pbot.repository.dailypidor;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDailyPidorRepository extends CrudRepository<DailyPidor, Long> {

  @Query("SELECT p FROM DailyPidor p WHERE p.chatId = ?1")
  List<DailyPidor> findByChatId(long chatId);
}
