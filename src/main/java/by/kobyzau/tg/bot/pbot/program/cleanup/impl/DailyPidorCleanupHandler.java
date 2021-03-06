package by.kobyzau.tg.bot.pbot.program.cleanup.impl;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.program.cleanup.CleanupHandler;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DailyPidorCleanupHandler implements CleanupHandler {
  @Autowired private Logger logger;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private TelegramService telegramService;

  @Override
  public void cleanup() {
    dailyPidorRepository.getAll().stream()
        .collect(Collectors.groupingBy(DailyPidor::getChatId))
        .forEach(this::cleanup);
  }

  private void cleanup(long chatId, List<DailyPidor> dailyPidors) {
    String chatName =
        telegramService
            .getChat(chatId)
            .map(c -> chatId + ": " + c.getTitle())
            .orElse(String.valueOf(chatId));
    LocalDate removeStartDate = DateUtil.now().minusWeeks(2);
    LocalDate notifyStartDate = DateUtil.now().minusWeeks(2).plusDays(2);
    LocalDate maxDate =
        dailyPidors.stream()
            .map(DailyPidor::getLocalDate)
            .max(Comparator.comparing(d -> d))
            .orElseGet(DateUtil::now);
    if (notifyStartDate.isEqual(maxDate)) {
      logger.warn("Will be removed Daily Pidor for chat " + chatName);
    } else if (removeStartDate.isAfter(maxDate)) {
      logger.warn("Removing Daily Pidor for chat " + chatName);
      dailyPidors.stream().map(DailyPidor::getId).forEach(dailyPidorRepository::delete);
    }
  }
}
