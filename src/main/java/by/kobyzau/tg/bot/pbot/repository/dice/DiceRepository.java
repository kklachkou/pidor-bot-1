package by.kobyzau.tg.bot.pbot.repository.dice;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorDice;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;

import java.util.List;

public interface DiceRepository extends CrudRepository<PidorDice> {
    List<PidorDice> getByChatId(long chatId);
}
