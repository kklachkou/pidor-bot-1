package by.kobyzau.tg.bot.pbot.repository.exclude;

import by.kobyzau.tg.bot.pbot.model.ExcludeGameUserValue;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IExcludeGameRepository extends CrudRepository<ExcludeGameUserValue, Long> {

  @Query("SELECT p FROM ExcludeGameUserValue p WHERE p.chatId = ?1")
  List<ExcludeGameUserValue> findByChatId(long chatId);
}
