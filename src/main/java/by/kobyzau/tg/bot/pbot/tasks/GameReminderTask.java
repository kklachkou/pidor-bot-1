package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

@Component("gameReminderTask")
public class GameReminderTask implements Task {

  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private CalendarSchedule calendarSchedule;

  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private Logger logger;
  @Autowired private BotService botService;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  private static List<String> messages =
      Arrays.asList(
          "Чё тупим, работаем",
          "Чёт вы сегодня тормозите...",
          "Ещё не все сыграли...",
          "Я думал вы тут все активы, а оказалось, что тут есть и пассивы...",
          "Давайте-давайте, поживее",
          "Мне вечность ждать?",
          "Ну и сколько я вас буду ждать?");

  @Override
  public void processTask() {
    sendInfo();
  }

  private void sendInfo() {
    logger.info("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    telegramService.getChatIds().stream()
        .filter(botService::isChatValid)
        .forEach(chatId -> executor.execute(() -> sendInfo(chatId)));
  }

  private void sendInfo(long chatId) {
    switch (calendarSchedule.getItem(chatId, DateUtil.now())) {
      case EXCLUDE_GAME:
      case EMOJI_GAME:
        if (dailyPidorRepository.getByChatAndDate(chatId, DateUtil.now()).isPresent()) {
          return;
        }
        botActionCollector.text(chatId, new RandomText(messages));
    }
  }
}
