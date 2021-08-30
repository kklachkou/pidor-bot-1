package by.kobyzau.tg.bot.pbot.repository.dice;

import by.kobyzau.tg.bot.pbot.model.PidorDice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDiceRepository extends CrudRepository<PidorDice, Long> {

  @Query("SELECT p FROM PidorDice p WHERE p.chatId = ?1")
  List<PidorDice> findByChatId(long chatId);
}
