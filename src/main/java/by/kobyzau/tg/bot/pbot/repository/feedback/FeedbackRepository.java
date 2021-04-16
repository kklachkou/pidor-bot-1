package by.kobyzau.tg.bot.pbot.repository.feedback;

import by.kobyzau.tg.bot.pbot.model.Feedback;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;

import java.util.List;

public interface FeedbackRepository extends CrudRepository<Feedback> {

  List<Feedback> getByChat(long chatId);
}
