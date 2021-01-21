package by.kobyzau.tg.bot.pbot.program.cleanup.impl;

import by.kobyzau.tg.bot.pbot.model.PidorDice;
import by.kobyzau.tg.bot.pbot.program.cleanup.CleanupHandler;
import by.kobyzau.tg.bot.pbot.repository.dice.DiceRepository;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DiceCleanupHandler implements CleanupHandler {

  @Autowired private DiceRepository diceRepository;

  @Override
  public void cleanup() {
    LocalDate startDate = DateUtil.now().minusWeeks(1);
    diceRepository.getAll().stream()
        .filter(d -> d.getLocalDate().isBefore(startDate))
        .map(PidorDice::getId)
        .forEach(diceRepository::delete);
  }
}
