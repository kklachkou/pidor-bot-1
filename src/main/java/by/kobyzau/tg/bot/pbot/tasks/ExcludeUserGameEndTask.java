package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.PidorFunnyAction;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.ExcludeGameUserValue;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.ExcludeGameService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component("excludeUserGameEndTask")
public class ExcludeUserGameEndTask implements Task {

  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private ExcludeGameService gameService;
  @Autowired private PidorService pidorService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private Logger logger;

  @Autowired private BotService botService;
  @Autowired private List<PidorFunnyAction> pidorFunnyActions;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {

    if (gameService.isExcludeGameDay(DateUtil.now())) {
      logger.info("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
      telegramService.getChatIds().stream()
          .filter(botService::isChatValid)
          .forEach(chatId -> executor.execute(() -> sendInfo(chatId)));
    }
  }

  private void sendInfo(long chatId) {
    try {
      if (dailyPidorRepository.getByChatAndDate(chatId, DateUtil.now()).isPresent()) {
        return;
      }
      Set<Integer> playedIds =
          gameService.getExcludeGameUserValues(chatId, DateUtil.now()).stream()
              .map(ExcludeGameUserValue::getPlayerTgId)
              .collect(Collectors.toSet());
      List<Pidor> notPlayedPidors =
          pidorService.getByChat(chatId).stream()
              .filter(p -> !playedIds.contains(p.getTgId()))
              .collect(Collectors.toList());
      if (CollectionUtil.isEmpty(notPlayedPidors)) {
        return;
      }
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.text(
          chatId,
          new ParametizedText(
              "{0} людей обладают крайне медлительной реакцией",
              new IntText(notPlayedPidors.size())));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.text(chatId, new SimpleText("Думаю кого-то из них нужно наказать"));
      processLastPidor(CollectionUtil.getRandomValue(notPlayedPidors));
    } catch (Exception e) {
      logger.error("Cannot end exclude user game for chat " + chatId, e);
    }
  }

  private void processLastPidor(Pidor pidor) {
    saveDailyPidor(pidor);
    botService.unpinLastBotMessage(pidor.getChatId());
    CollectionUtil.getRandomValue(pidorFunnyActions).processFunnyAction(pidor.getChatId(), pidor);
  }

  private void saveDailyPidor(Pidor pidor) {
    DailyPidor dailyPidor = new DailyPidor();
    dailyPidor.setChatId(pidor.getChatId());
    dailyPidor.setPlayerTgId(pidor.getTgId());
    dailyPidor.setLocalDate(DateUtil.now());
    dailyPidorRepository.create(dailyPidor);
  }
}
