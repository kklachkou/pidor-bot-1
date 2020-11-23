package by.kobyzau.tg.bot.pbot.repository.digestusage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import by.kobyzau.tg.bot.pbot.model.DigestUsage;

@Component
@Profile("prod")
public class DBDigestUsageRepository implements DigestUsageRepository {

  @Autowired
  private IDigestUsageRepository repository;

  @Override
  public List<DigestUsage> findByType(String type) {
    return repository.findByType(type);
  }

  @Override
  public long create(DigestUsage obj) {
    return repository.save(obj).getId();
  }

  @Override
  public void update(DigestUsage obj) {
    repository.save(obj);
  }

  @Override
  public DigestUsage get(long id) {
    return repository.findById(id).orElse(null);
  }

  @Override
  public List<DigestUsage> getAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    repository.deleteById(id);
  }
}
