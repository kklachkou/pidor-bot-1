package by.kobyzau.tg.bot.pbot.repository.exclude;

import by.kobyzau.tg.bot.pbot.model.ExcludeGameUserValue;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;

import java.util.List;

public interface ExcludeGameRepository extends CrudRepository<ExcludeGameUserValue> {

  List<ExcludeGameUserValue> getByChatId(long chatId);
}
