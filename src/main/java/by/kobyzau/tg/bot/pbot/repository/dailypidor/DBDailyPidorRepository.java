package by.kobyzau.tg.bot.pbot.repository.dailypidor;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Profile("prod")
public class DBDailyPidorRepository implements DailyPidorRepository {

  @Autowired private IDailyPidorRepository dailyPidorRepository;

  @Override
  public List<DailyPidor> getByChat(long chatId) {
    return dailyPidorRepository.findByChatId(chatId);
  }

  @Override
  public Optional<DailyPidor> getByChatAndDate(long chatId, LocalDate localDate) {
    return getByChat(chatId).stream()
        .filter(d -> DateUtil.equals(localDate, d.getLocalDate()))
        .findFirst();
  }

  @Override
  public long create(DailyPidor obj) {
    return dailyPidorRepository.save(obj).getId();
  }

  @Override
  public void update(DailyPidor obj) {
    dailyPidorRepository.save(obj);
  }

  @Override
  public DailyPidor get(long id) {
    return dailyPidorRepository.findById(id).orElse(null);
  }

  @Override
  public List<DailyPidor> getAll() {
    return StreamSupport.stream(dailyPidorRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    dailyPidorRepository.deleteById(id);
  }
}
