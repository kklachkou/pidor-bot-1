package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.model.QuestionnaireAnswer;
import by.kobyzau.tg.bot.pbot.repository.questionnaire.QuestionnaireAnswerRepository;
import by.kobyzau.tg.bot.pbot.service.QuestionnaireService;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {
  @Autowired private QuestionnaireAnswerRepository repository;

  @Override
  public void addUniqueAnswer(QuestionnaireAnswer answer) {
    repository.getByChat(answer.getChatId()).stream()
        .filter(f -> Objects.equals(f.getPlayerTgId(), answer.getPlayerTgId()))
        .filter(f -> Objects.equals(f.getType(), answer.getType()))
        .map(QuestionnaireAnswer::getId)
        .forEach(repository::delete);
    repository.create(answer);
  }

  @Override
  public List<QuestionnaireAnswer> getAnswers(long chatId) {
    return repository.getByChat(chatId);
  }
}
