package by.kobyzau.tg.bot.pbot.repository.telegraph;

import by.kobyzau.tg.bot.pbot.model.TelegraphPage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITelegraphPageRepository extends CrudRepository<TelegraphPage, Long> {}
