package by.kobyzau.tg.bot.pbot.repository.questionnaire;

import by.kobyzau.tg.bot.pbot.model.QuestionnaireAnswer;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface IQuestionnaireAnswerRepository
  //      extends CrudRepository<QuestionnaireAnswer, Long>
{
  //@Query("SELECT f FROM QuestionnaireAnswer f WHERE f.chatId = ?1")
  List<QuestionnaireAnswer> findByChatId(long chatId);
}
