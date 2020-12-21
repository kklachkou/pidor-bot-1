package by.kobyzau.tg.bot.pbot.repository.custom;

import by.kobyzau.tg.bot.pbot.model.CustomDailyUserData;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Profile("prod")
public class DBCustomDailyDataRepository implements CustomDailyDataRepository {

  @Autowired private ICustomDailyDataRepository repository;

  @Override
  public List<CustomDailyUserData> getByChat(long chatId) {
    return repository.findByChatId(chatId);
  }

  @Override
  public List<CustomDailyUserData> getByChatAndDate(long chatId, LocalDate localDate) {
    return getByChat(chatId).stream()
        .filter(d -> DateUtil.equals(localDate, d.getLocalDate()))
        .collect(Collectors.toList());
  }

  @Override
  public long create(CustomDailyUserData obj) {
    return repository.save(obj).getId();
  }

  @Override
  public void update(CustomDailyUserData obj) {
    repository.save(obj);
  }

  @Override
  public CustomDailyUserData get(long id) {
    return repository.findById(id).orElse(null);
  }

  @Override
  public List<CustomDailyUserData> getAll() {
    return StreamSupport.stream(repository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    repository.deleteById(id);
  }
}
