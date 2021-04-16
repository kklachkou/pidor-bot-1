package by.kobyzau.tg.bot.pbot.repository.pidor;

import java.util.List;
import java.util.Optional;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;

public interface PidorRepository extends CrudRepository<Pidor> {

  Optional<Pidor> getByChatAndPlayerTgId(long chatId, long tgId);

  List<Pidor> getByChat(long chatId);
}
