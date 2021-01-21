package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.AlreadyFoundPidorProcessor;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.NewPidorProcessor;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class PidorCommandHandler implements CommandHandler {

  @Autowired private AlreadyFoundPidorProcessor alreadyFoundPidorProcessor;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private NewPidorProcessor newPidorProcessor;

  @Autowired private CalendarSchedule calendarSchedule;

  @Autowired private PidorService pidorService;

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private CommandHandlerFactory commandHandlerFactory;

  private final Map<String, LocalDateTime> lastCallsCache = new HashMap<>();

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    Optional<DailyPidor> dailyPidor =
        dailyPidorRepository.getByChatAndDate(chatId, DateUtil.now());
    if (dailyPidor.isPresent()) {
      alreadyFoundPidorProcessor.processAlreadyFound(message, dailyPidor.get());
    } else if (isGameDay(chatId)) {
      commandHandlerFactory.getHandler(Command.GAME).processCommand(message, text);
    } else if (isElectionDay(chatId)) {
      commandHandlerFactory.getHandler(Command.ELECTION).processCommand(message, text);
    } else {
      newPidorProcessor.processNewDailyPidor(message);
    }
  }

  private boolean isElectionDay(long chatId) {
    return calendarSchedule.getItem(chatId, DateUtil.now()) == ScheduledItem.ELECTION;
  }

  private boolean isGameDay(long chatId) {
    return calendarSchedule.getItem(chatId, DateUtil.now()) == ScheduledItem.EMOJI_GAME
        || calendarSchedule.getItem(chatId, DateUtil.now()) == ScheduledItem.EXCLUDE_GAME;
  }

  @Override
  public Command getCommand() {
    return Command.PIDOR;
  }
}
