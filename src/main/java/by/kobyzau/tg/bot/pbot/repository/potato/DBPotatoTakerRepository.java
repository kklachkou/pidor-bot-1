package by.kobyzau.tg.bot.pbot.repository.potato;

import by.kobyzau.tg.bot.pbot.model.HotPotatoTaker;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class DBPotatoTakerRepository implements PotatoTakerRepository {

  @Autowired private IPotatoTakerRepository repository;

  @Override
  public List<HotPotatoTaker> getByChatAndDate(long chatId, LocalDate date) {
    return repository.findByChatId(chatId).stream()
        .filter(p -> p.getDate().equals(date))
        .collect(Collectors.toList());
  }

  @Override
  public long create(HotPotatoTaker obj) {
    return repository.save(obj).getId();
  }

  @Override
  public void update(HotPotatoTaker obj) {
    repository.save(obj);
  }

  @Override
  public HotPotatoTaker get(long id) {
    return repository.findById(id).orElse(null);
  }

  @Override
  public List<HotPotatoTaker> getAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    repository.deleteById(id);
  }
}
