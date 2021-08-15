package by.kobyzau.tg.bot.pbot.repository.questionnaire;

import by.kobyzau.tg.bot.pbot.model.QuestionnaireAnswer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class DBQuestionnaireAnswerRepository implements QuestionnaireAnswerRepository {
  @Autowired private IQuestionnaireAnswerRepository repository;

  @Override
  public List<QuestionnaireAnswer> getByChat(long chatId) {
    return repository.findByChatId(chatId);
  }

  @Override
  public long create(QuestionnaireAnswer obj) {
    return repository.save(obj).getId();
  }

  @Override
  public void update(QuestionnaireAnswer obj) {
    repository.save(obj);
  }

  @Override
  public QuestionnaireAnswer get(long id) {
    return repository.findById(id).orElse(null);
  }

  @Override
  public List<QuestionnaireAnswer> getAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    repository.deleteById(id);
  }
}
