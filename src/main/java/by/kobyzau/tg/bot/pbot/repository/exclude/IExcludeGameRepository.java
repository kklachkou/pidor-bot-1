package by.kobyzau.tg.bot.pbot.repository.exclude;

import by.kobyzau.tg.bot.pbot.model.ExcludeGameUserValue;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IExcludeGameRepository extends CrudRepository<ExcludeGameUserValue, Long> {
}
