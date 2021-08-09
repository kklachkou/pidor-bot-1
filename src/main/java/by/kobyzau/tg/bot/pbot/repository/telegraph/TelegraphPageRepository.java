package by.kobyzau.tg.bot.pbot.repository.telegraph;

import by.kobyzau.tg.bot.pbot.model.TelegraphPage;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;
import java.util.Optional;

public interface TelegraphPageRepository extends CrudRepository<TelegraphPage> {

    Optional<TelegraphPage> getPageByLinkedId(String linkedId);

    long create(TelegraphPage page);

    void update(TelegraphPage page);
}
