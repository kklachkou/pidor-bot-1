package by.kobyzau.tg.bot.pbot.repository.pidor;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRepoPidorRepository extends CrudRepository<Pidor, Long> {

  @Query("SELECT p FROM Pidor p WHERE p.chatId = ?1")
  List<Pidor> findByChatId(long chatId);

  @Query("SELECT distinct p.chatId FROM Pidor p")
  List<Long> getChatIds();
}
