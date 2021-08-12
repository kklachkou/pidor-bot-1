package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.HotPotatoDto;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.BoldText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.TimeLeftText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.HotPotatoesService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.HotPotatoUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component("hotPotatoAutoStrikeTask")
public class HotPotatoAutoStrikeTask implements Task {
  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private HotPotatoesService hotPotatoesService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private PidorService pidorService;

  @Autowired private Logger logger;
  @Autowired private HotPotatoUtil hotPotatoUtil;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    LocalDate now = DateUtil.now();
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    telegramService.getChatIds().stream()
        .filter(chatId -> hotPotatoesService.isHotPotatoesDay(chatId, now))
        .forEach(chatId -> executor.execute(() -> sendMessage(chatId)));
  }

  private void sendMessage(long chatId) {
    if (dailyPidorRepository.getByChatAndDate(chatId, DateUtil.now()).isPresent()
        || hotPotatoesService.getLastTaker(DateUtil.now(), chatId).isPresent()) {
      return;
    }
    Pidor newPidor = CollectionUtil.getRandomValue(pidorService.getByChat(chatId));
    LocalDateTime newDeadline = hotPotatoesService.setNewTaker(newPidor);
    botActionCollector.add(
        new SendMessageBotAction(
                chatId,
                new ParametizedText(
                    hotPotatoUtil.getPotatoesTakerMessage(),
                    new FullNamePidorText(newPidor),
                    new BoldText(new TimeLeftText(DateUtil.currentTime(), newDeadline))))
            .withReplyMarkup(
                InlineKeyboardMarkup.builder()
                    .keyboardRow(
                        Collections.singletonList(
                            InlineKeyboardButton.builder()
                                .text("Перекинуть картошечку")
                                .callbackData(StringUtil.serialize(new HotPotatoDto()))
                                .build()))
                    .build()));
  }
}
