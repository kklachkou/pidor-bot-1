package by.kobyzau.tg.bot.pbot.repository.potato;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.HotPotatoTaker;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface IPotatoTakerRepository extends CrudRepository<HotPotatoTaker, Long> {

    @Query("SELECT p FROM HotPotatoTaker p WHERE p.chatId = ?1")
    List<HotPotatoTaker> findByChatId(long chatId);
}
