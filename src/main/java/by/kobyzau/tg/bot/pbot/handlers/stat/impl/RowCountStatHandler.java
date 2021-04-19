package by.kobyzau.tg.bot.pbot.handlers.stat.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.stat.StatHandler;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RowCountStatHandler implements StatHandler {
  @Autowired private BotActionCollector botActionCollector;

  @Autowired private List<CrudRepository<?>> repositories;

  @Override
  public void printStat(long chatId) {
    int totalCount = 0;
    for (CrudRepository<?> repository : repositories) {
      int size = repository.getAll().size();
      totalCount += size;
      botActionCollector.text(
          chatId,
          new ParametizedText(
              "{0}: {1}",
              new SimpleText(repository.getClass().getSimpleName()), new IntText(size)));
    }
    botActionCollector.text(chatId, new ParametizedText("Total: {0}", new IntText(totalCount)));
  }

  @Override
  public StatType getType() {
    return StatType.ROW_COUNT;
  }
}
