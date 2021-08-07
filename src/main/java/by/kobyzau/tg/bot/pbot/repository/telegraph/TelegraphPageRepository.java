package by.kobyzau.tg.bot.pbot.repository.telegraph;

import by.kobyzau.tg.bot.pbot.model.TelegraphPage;
import java.util.Optional;

public interface TelegraphPageRepository {

    Optional<TelegraphPage> getPageByLinkedId(String linkedId);

    long create(TelegraphPage page);

    void update(TelegraphPage page);
}
