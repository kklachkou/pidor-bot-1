package by.kobyzau.tg.bot.pbot.repository.telegraph;

import by.kobyzau.tg.bot.pbot.model.TelegraphPage;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component
public class DBTelegraphRepository implements TelegraphPageRepository {

  @Autowired private ITelegraphPageRepository repository;

  @Override
  public Optional<TelegraphPage> getPageByLinkedId(String linkedId) {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .filter(o -> StringUtil.equals(o.getLinkedId(), linkedId))
        .findFirst();
  }

  @Override
  public long create(TelegraphPage page) {
    return repository.save(page).getId();
  }

  @Override
  public void update(TelegraphPage page) {
    repository.save(page);
  }

  @Override
  public TelegraphPage get(long id) {
    return repository.findById(id).orElse(null);
  }

  @Override
  public List<TelegraphPage> getAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    repository.deleteById(id);
  }
}
