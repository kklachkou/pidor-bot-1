package by.kobyzau.tg.bot.pbot.repository.pidorofyear;

import by.kobyzau.tg.bot.pbot.model.PidorOfYear;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPidorOfYearRepository extends CrudRepository<PidorOfYear, Long> {
    @Query("SELECT p FROM PidorOfYear p WHERE p.chatId = ?1")
    List<PidorOfYear> findByChatId(long chatId);
}
