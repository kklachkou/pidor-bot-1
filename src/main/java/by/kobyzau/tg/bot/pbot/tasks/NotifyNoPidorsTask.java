package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNameLinkedPidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executor;

@Component("notifyNoPidors")
public class NotifyNoPidorsTask implements Task {

  @Value("${app.admin.userId}")
  private long adminUserId;

  private final Selection<Text> notifyMessage =
      new ConsistentSelection<>(
              "Вы там совсем того? Забыли узнать кто сегодня /pidor",
              "Вы не забыли про меня? давайте играть - /pidor",
              "Эй, вы где? Играйте - /pidor",
              "Если вы забыли, нужно нажать /pidor",
              "Сегодня вы еще не играли. Жми /pidor")
          .map(SimpleText::new);
  @Autowired private PidorRepository pidorRepository;

  @Autowired private DailyPidorRepository dailyPidorRepository;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private Logger logger;

  @Autowired private CalendarSchedule calendarSchedule;

  @Autowired private TelegramService telegramService;

  @Autowired private BotService botService;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    telegramService.getChatIds().stream()
        .filter(botService::isChatValid)
        .forEach(chatId -> executor.execute(() -> processNotify(chatId)));
  }

  private void processNotify(long chatId) {
    Optional<Pidor> pidor = getPidorOfTheDay(chatId);
    if (pidor.isPresent()) {
      boolean notify = CollectionUtil.getRandomValue(Arrays.asList(true, false, false));
      if (notify) {
        notifyPidor(pidor.get());
      }
    } else {
      ScheduledItem scheduledItem = calendarSchedule.getItem(chatId, DateUtil.now());
      if (chatId != adminUserId && scheduledItem.isRequireManualStart()) {
        botActionCollector.text(chatId, notifyMessage.next());
      }
    }
  }

  private Optional<Pidor> getPidorOfTheDay(long chatId) {
    LocalDate now = DateUtil.now();
    return dailyPidorRepository
        .getByChatAndDate(chatId, now)
        .map(DailyPidor::getPlayerTgId)
        .flatMap(userId -> pidorRepository.getByChatAndPlayerTgId(chatId, userId));
  }

  private void notifyPidor(Pidor pidor) {
    Selection<Text> messages =
        new ConsistentSelection<>(
                "А вы всё еще помните что {0} сегодня пидор дня?",
                "Если кто-то уже забыл, {0} сегодня пидор дня",
                "Думаю стоит напомнить вам, что {0} сегодня пидор дня",
                "{0} стал пидором дня. Но думаю, вы и сами об этом знаете",
                "Хочу напомнить, что {0} сегодня пидор дня:)",
                "Что-то тихо у вас. Ну давайте хотябы пообсуждаем почему {0} сегодня пидор дня",
                "Что, что говорите? {0} сегодня был пидором дня? Круто!",
                "Ведь никто же не сомневался, что {0} сегодня пидор?",
                "{0} - как твои дела?",
                "{0} - как твои делишки?",
                "Не напомните почему {0} сегодня пидор дня?",
                "Если кто-то спросит меня, кто сегодня пидор дня, я скажу - {0}",
                "{0} - пидор дня. Люблю это повторять:)",
                "Просто напомню, что {0} сегодня пидор дня")
            .map(SimpleText::new);
    botActionCollector.text(
        pidor.getChatId(),
            new ParametizedText(messages.next(), new ShortNameLinkedPidorText(pidor)));
    botActionCollector.wait(pidor.getChatId(), ChatAction.TYPING);
    botActionCollector.sticker(pidor.getChatId(), StickerType.LOVE.getRandom());
  }
}
