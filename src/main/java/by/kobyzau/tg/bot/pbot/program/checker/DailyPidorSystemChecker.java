package by.kobyzau.tg.bot.pbot.program.checker;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Component
public class DailyPidorSystemChecker implements SystemChecker {

  @Autowired private Logger logger;

  @Autowired private TelegramService telegramService;

  @Autowired private BotService botService;

  @Autowired private DailyPidorRepository dailyPidorRepository;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @Override
  public void check() {
    telegramService.getChatIds()
        .forEach(chatId -> executor.execute(() -> check(chatId)));
  }

  private void check(long chatId) {
    List<DailyPidor> dailyPidors = dailyPidorRepository.getByChat(chatId);
    Map<LocalDate, DailyPidor> dateListMap = new HashMap<>();
    for (DailyPidor dailyPidor : dailyPidors) {
      DailyPidor foundDaily = dateListMap.get(dailyPidor.getLocalDate());
      if (foundDaily != null) {
        warn(chatId, dailyPidor, foundDaily);
      } else {
        dateListMap.put(dailyPidor.getLocalDate(), dailyPidor);
      }
    }
  }

  private void warn(long chatId, DailyPidor d1, DailyPidor d2) {
    String message =
        new TextBuilder(
                new ParametizedText(
                    "! #CHECKER Found duplicate daily pidor for chat {0}", new LongText(chatId)))
            .append(new NewLineText())
            .append(
                new ParametizedText(
                    "Daily 1 - id:{0}, date:{1}, pidor:{2}",
                    new LongText(d1.getId()),
                    new DateText(d1.getLocalDate()),
                    new LongText(d1.getPlayerTgId())))
            .append(new NewLineText())
            .append(
                new ParametizedText(
                    "Daily 2 - id:{0}, date:{1}, pidor:{2}",
                    new LongText(d2.getId()),
                    new DateText(d2.getLocalDate()),
                    new LongText(d2.getPlayerTgId())))
            .text();
    System.out.println(message);
    logger.warn(message);
  }
}
