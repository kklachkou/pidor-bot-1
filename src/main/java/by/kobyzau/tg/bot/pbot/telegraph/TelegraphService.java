package by.kobyzau.tg.bot.pbot.telegraph;

import by.kobyzau.tg.bot.pbot.model.TelegraphPage;
import java.util.List;
import org.telegram.telegraph.api.objects.Node;

public interface TelegraphService {

    TelegraphPage getPage(String linkedId);

    void createPageIfNotExist(String linkedId);

    void updatePage(String linkedId, String title, List<Node> content);
}
