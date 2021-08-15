package by.kobyzau.tg.bot.pbot.program.cleanup.impl;

import by.kobyzau.tg.bot.pbot.model.QuestionnaireAnswer;
import by.kobyzau.tg.bot.pbot.program.cleanup.CleanupHandler;
import by.kobyzau.tg.bot.pbot.repository.questionnaire.QuestionnaireAnswerRepository;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestionnaireCleanupHandler implements CleanupHandler {
  @Autowired private QuestionnaireAnswerRepository repository;

  @Autowired
  @Override
  public void cleanup() {
    LocalDate startDate = DateUtil.now().minusWeeks(1);
    repository.getAll().stream()
        .filter(c -> startDate.isAfter(c.getDate()))
        .mapToLong(QuestionnaireAnswer::getId)
        .forEach(repository::delete);
  }
}
