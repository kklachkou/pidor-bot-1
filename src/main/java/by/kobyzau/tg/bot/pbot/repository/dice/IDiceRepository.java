package by.kobyzau.tg.bot.pbot.repository.dice;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import by.kobyzau.tg.bot.pbot.model.PidorDice;

@Repository
public interface IDiceRepository extends CrudRepository<PidorDice, Long> {
}
