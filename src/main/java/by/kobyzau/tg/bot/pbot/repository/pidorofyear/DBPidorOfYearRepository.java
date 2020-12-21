package by.kobyzau.tg.bot.pbot.repository.pidorofyear;

import by.kobyzau.tg.bot.pbot.model.PidorOfYear;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Profile("prod")
public class DBPidorOfYearRepository implements PidorOfYearRepository {

  @Autowired private IPidorOfYearRepository repository;

  @Override
  public long create(PidorOfYear obj) {
    return repository.save(obj).getId();
  }

  @Override
  public void update(PidorOfYear obj) {
    repository.save(obj);
  }

  @Override
  public PidorOfYear get(long id) {
    return repository.findById(id).orElse(null);
  }

  @Override
  public List<PidorOfYear> getAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public List<PidorOfYear> getPidorOfYearByChat(long chatId) {
    return repository.findByChatId(chatId);
  }

  @Override
  public void delete(long id) {
    repository.deleteById(id);
  }
}
