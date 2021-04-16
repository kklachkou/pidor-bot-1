package by.kobyzau.tg.bot.pbot.repository.feedback;

import by.kobyzau.tg.bot.pbot.model.Feedback;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IFeedbackRepository extends CrudRepository<Feedback, Long> {
    @Query("SELECT f FROM Feedback f WHERE f.chatId = ?1")
    List<Feedback> findByChatId(long chatId);
}
