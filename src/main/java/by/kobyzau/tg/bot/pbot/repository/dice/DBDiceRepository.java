package by.kobyzau.tg.bot.pbot.repository.dice;

import by.kobyzau.tg.bot.pbot.model.PidorDice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Profile("prod")
public class DBDiceRepository implements DiceRepository {

  @Autowired private IDiceRepository diceRepository;

  @Override
  public List<PidorDice> getByChatId(long chatId) {
    return diceRepository.findByChatId(chatId);
  }

  @Override
  public long create(PidorDice obj) {
    return diceRepository.save(obj).getId();
  }

  @Override
  public void update(PidorDice obj) {
    diceRepository.save(obj);
  }

  @Override
  public PidorDice get(long id) {
    return diceRepository.findById(id).orElse(null);
  }

  @Override
  public List<PidorDice> getAll() {
    return StreamSupport.stream(diceRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(long id) {
    diceRepository.deleteById(id);
  }
}
