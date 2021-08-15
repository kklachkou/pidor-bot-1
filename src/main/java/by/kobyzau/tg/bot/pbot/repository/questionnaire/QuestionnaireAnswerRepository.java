package by.kobyzau.tg.bot.pbot.repository.questionnaire;

import by.kobyzau.tg.bot.pbot.model.Feedback;
import by.kobyzau.tg.bot.pbot.model.QuestionnaireAnswer;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;
import java.util.List;

public interface QuestionnaireAnswerRepository extends CrudRepository<QuestionnaireAnswer> {

  List<QuestionnaireAnswer> getByChat(long chatId);
}
