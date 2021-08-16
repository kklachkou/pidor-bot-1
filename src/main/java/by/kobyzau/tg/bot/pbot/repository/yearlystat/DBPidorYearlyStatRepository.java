package by.kobyzau.tg.bot.pbot.repository.yearlystat;

import by.kobyzau.tg.bot.pbot.model.PidorYearlyStat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class DBPidorYearlyStatRepository implements PidorYearlyStatRepository {
  @Autowired private IPidorYearlyStatRepository repository;

  @Override
  public List<PidorYearlyStat> getByChatId(long chatId) {
    return repository.findByChatId(chatId);
  }

  @Override
  public long create(PidorYearlyStat obj) {
    return repository.save(obj).getId();
  }

  @Override
  public void update(PidorYearlyStat obj) {
    repository.save(obj);
  }

  @Override
  public PidorYearlyStat get(long id) {
    return repository.findById(id).orElse(null);
  }

  @Override
  public List<PidorYearlyStat> getAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    repository.deleteById(id);
  }
}
