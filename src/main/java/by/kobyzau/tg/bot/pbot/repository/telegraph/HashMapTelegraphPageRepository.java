package by.kobyzau.tg.bot.pbot.repository.telegraph;

import by.kobyzau.tg.bot.pbot.model.TelegraphPage;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class HashMapTelegraphPageRepository implements TelegraphPageRepository {

  private final Map<Long, TelegraphPage> map = new HashMap<>();

  @Override
  public Optional<TelegraphPage> getPageByLinkedId(String linkedId) {
    return map.values().stream()
        .filter(v -> StringUtil.equals(v.getLinkedId(), linkedId))
        .map(TelegraphPage::new)
        .findFirst();
  }

  @Override
  public void update(TelegraphPage page) {
    map.put(page.getId(), new TelegraphPage(page));
  }

  @Override
  public long create(TelegraphPage page) {
    long newId = getNewId();
    map.put(newId, new TelegraphPage(page));
    return newId;
  }

  private synchronized long getNewId() {
    long newId = map.keySet().stream().max(Long::compareTo).orElse(1L) + 1;
    map.put(newId, null);
    return newId;
  }
}
