package by.kobyzau.tg.bot.pbot.program.checker;

import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RowCountSystemChecker implements SystemChecker {

  private static final int FREE_TIER = 10000;
  @Autowired private List<CrudRepository<?>> repositories;
  @Autowired private Logger logger;

  @Override
  public void check() {
    int count = repositories.stream().map(CrudRepository::getAll).mapToInt(List::size).sum();
    if (count > FREE_TIER * 0.85) {
      logger.warn("! #CHECKER\nRow count is " + count);
    }
  }
}
