package by.kobyzau.tg.bot.pbot.repository.custom;

import by.kobyzau.tg.bot.pbot.model.CustomDailyUserData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICustomDailyDataRepository extends CrudRepository<CustomDailyUserData, Long> {

  @Query("SELECT p FROM CustomDailyUserData p WHERE p.chatId = ?1")
  List<CustomDailyUserData> findByChatId(long chatId);
}
