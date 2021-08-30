package by.kobyzau.tg.bot.pbot.repository.pidor;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Profile("prod")
public class DbPidorRepository implements PidorRepository {

  @Autowired private IRepoPidorRepository pidorRepository;


  @Override
  public List<Long> getChatIdsWithPidors() {
    return pidorRepository.getChatIds();
  }

  @Override
  public Optional<Pidor> getByChatAndPlayerTgId(long chatId, long tgId) {
    return pidorRepository.findByChatId(chatId).stream()
        .filter(p -> p.getTgId() == tgId)
        .findFirst();
  }

  @Override
  public List<Pidor> getByChat(long chatId) {
    return pidorRepository.findByChatId(chatId);
  }

  @Override
  public long create(Pidor obj) {
    return pidorRepository.save(obj).getId();
  }

  @Override
  public void update(Pidor obj) {
    pidorRepository.save(obj);
  }

  @Override
  public Pidor get(long id) {
    return pidorRepository.findById(id).orElse(null);
  }

  @Override
  public List<Pidor> getAll() {
    return StreamSupport.stream(pidorRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    pidorRepository.deleteById(id);
  }
}
