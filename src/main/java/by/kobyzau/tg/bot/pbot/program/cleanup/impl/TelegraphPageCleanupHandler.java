package by.kobyzau.tg.bot.pbot.program.cleanup.impl;

import by.kobyzau.tg.bot.pbot.model.TelegraphPage;
import by.kobyzau.tg.bot.pbot.program.cleanup.CleanupHandler;
import by.kobyzau.tg.bot.pbot.repository.telegraph.TelegraphPageRepository;
import by.kobyzau.tg.bot.pbot.telegraph.TelegraphType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TelegraphPageCleanupHandler implements CleanupHandler {

  @Autowired private TelegraphPageRepository telegraphPageRepository;

  private final Set<TelegraphType> cleanupTypes =
      new HashSet<>(Arrays.asList(TelegraphType.TEMP, TelegraphType.STATISTIC));

  @Override
  public void cleanup() {
    cleanupTempPages();
  }

  private void cleanupTempPages() {
    List<TelegraphPage> pages = telegraphPageRepository.getAll();
    for (TelegraphPage page : pages) {
      String linkedId = page.getLinkedId();
      TelegraphType telegraphType =
          TelegraphType.parseByLinkedId(linkedId).orElse(TelegraphType.TEMP);
      if (cleanupTypes.contains(telegraphType)) {
        telegraphPageRepository.delete(page.getId());
      }
    }
  }
}
