package by.kobyzau.tg.bot.pbot.repository.feedback;

import by.kobyzau.tg.bot.pbot.model.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Profile("prod")
public class DBFeedbackRepository implements FeedbackRepository {
  @Autowired private IFeedbackRepository repository;

  @Override
  public List<Feedback> getByChat(long chatId) {
    return repository.findByChatId(chatId);
  }

  @Override
  public long create(Feedback obj) {
    return repository.save(obj).getId();
  }

  @Override
  public void update(Feedback obj) {
    repository.save(obj);
  }

  @Override
  public Feedback get(long id) {
    return repository.findById(id).orElse(null);
  }

  @Override
  public List<Feedback> getAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    repository.deleteById(id);
  }
}
