package by.kobyzau.tg.bot.pbot.program.cleanup.impl;

import by.kobyzau.tg.bot.pbot.model.Feedback;
import by.kobyzau.tg.bot.pbot.program.cleanup.CleanupHandler;
import by.kobyzau.tg.bot.pbot.repository.feedback.FeedbackRepository;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class FeedbackCleanupHandler implements CleanupHandler {

  @Autowired private FeedbackRepository feedbackRepository;

  @Override
  public void cleanup() {
    LocalDate startDate = DateUtil.now().minusWeeks(1);
    feedbackRepository.getAll().stream()
        .filter(c -> startDate.isAfter(c.getUpdated()))
        .mapToLong(Feedback::getId)
        .forEach(feedbackRepository::delete);
  }
}
