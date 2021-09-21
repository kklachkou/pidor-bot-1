package by.kobyzau.tg.bot.pbot.handlers.stat.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.stat.StatHandler;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.LongText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class DeadDataStatHandler implements StatHandler {
  private final DailyPidorRepository dailyPidorRepository;
  private final PidorService pidorService;
  private final PidorRepository pidorRepository;
  private final BotActionCollector botActionCollector;

  @Override
  public void printStat(long chatId) {
    Set<Long> chatIds = new HashSet<>(pidorRepository.getChatIdsWithPidors());
    List<DailyPidor> dailyPidors = dailyPidorRepository.getAll();
    Map<Long, Integer> numDead = new HashMap<>();
    for (DailyPidor dailyPidor : dailyPidors) {
      if (chatIds.contains(dailyPidor.getChatId())) {
        continue;
      }
      int num = numDead.getOrDefault(dailyPidor.getChatId(), 0);
      numDead.put(dailyPidor.getChatId(), num + 1);
    }
    for (Long deadChatId : numDead.keySet()) {
      botActionCollector.text(
          chatId,
          new ParametizedText(
              "Dead Daily for chat {0}: {1}",
              new LongText(deadChatId), new IntText(numDead.getOrDefault(deadChatId, 0))));
    }

    for (Long pidorChatId : chatIds) {
      if (pidorService.getByChat(pidorChatId).size() > 0) {
        continue;
      }
      botActionCollector.text(
          chatId, new ParametizedText("Dead Pidor for chat {0}", new LongText(pidorChatId)));
    }
  }

  @Override
  public StatType getType() {
    return StatType.DEAD_DATA;
  }
}
