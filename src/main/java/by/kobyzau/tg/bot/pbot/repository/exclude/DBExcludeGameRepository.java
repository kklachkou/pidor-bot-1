package by.kobyzau.tg.bot.pbot.repository.exclude;

import by.kobyzau.tg.bot.pbot.model.ExcludeGameUserValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Profile("prod")
public class DBExcludeGameRepository implements ExcludeGameRepository {

  @Autowired private IExcludeGameRepository repository;

  @Override
  public long create(ExcludeGameUserValue obj) {
    return repository.save(obj).getId();
  }

  @Override
  public void update(ExcludeGameUserValue obj) {
    repository.save(obj);
  }

  @Override
  public ExcludeGameUserValue get(long id) {
    return repository.findById(id).orElse(null);
  }

  @Override
  public List<ExcludeGameUserValue> getAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    repository.deleteById(id);
  }
}
