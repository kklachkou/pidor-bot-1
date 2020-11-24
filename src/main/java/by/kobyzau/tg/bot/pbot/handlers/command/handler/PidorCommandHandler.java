package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.AlreadyFoundPidorProcessor;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.NewPidorProcessor;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.AssassinInlineMessageDto;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.ReplyKeyboardBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PidorCommandHandler implements CommandHandler {

  @Autowired private AlreadyFoundPidorProcessor alreadyFoundPidorProcessor;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private NewPidorProcessor newPidorProcessor;

  @Autowired private CalendarSchedule calendarSchedule;

  @Autowired private PidorService pidorService;

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    Optional<DailyPidor> dailyPidor =
        dailyPidorRepository.getByChatAndDate(message.getChatId(), DateUtil.now());
    if (dailyPidor.isPresent()) {
      alreadyFoundPidorProcessor.processAlreadyFound(message, dailyPidor.get());
    } else if (isAssassinDay()) {
      processAssassinCommand(message);
    } else {
      newPidorProcessor.processNewDailyPidor(message);
    }
  }

  private boolean isAssassinDay() {
    return calendarSchedule.getItem(DateUtil.now()) == ScheduledItem.ASSASSIN;
  }

  public void processAssassinCommand(Message message) {
    long chatId = message.getChatId();
    Optional<Pidor> callerPidor = pidorService.getPidor(chatId, message.getFrom().getId());
    if (!callerPidor.isPresent()) {
      botActionCollector.text(
          chatId, new SimpleText("Сначала зарегестрируйся"), message.getMessageId());
      return;
    }
    List<Pidor> pidors = CollectionUtil.getRandomList(pidorService.getByChat(chatId));
    if (pidors.size() < 2) {
      botActionCollector.text(chatId, new SimpleText("В чате слишком мало людей для этой игры"));
      botActionCollector.wait(chatId, 1, ChatAction.TYPING);
      botActionCollector.sticker(chatId, StickerType.SAD.getRandom());
      return;
    }

    InlineKeyboardMarkup.InlineKeyboardMarkupBuilder keyboardMarkupBuilder =
        InlineKeyboardMarkup.builder();
    String requestId = UUID.randomUUID().toString().substring(19);
    pidors.stream()
        .limit(10)
        .map(
            p ->
                InlineKeyboardButton.builder()
                    .text(new ShortNamePidorText(p).text())
                    .callbackData(
                        StringUtil.serialize(
                            new AssassinInlineMessageDto(
                                requestId, p.getTgId(), callerPidor.get().getTgId())))
                    .build())
        .map(Collections::singletonList)
        .forEach(keyboardMarkupBuilder::keyboardRow);

    botActionCollector.add(
        new ReplyKeyboardBotAction(
            chatId,
            new ParametizedText(
                new RandomText(
                    "{0}, ну что, кого ты выберешь пидором?",
                    "{0}, выберешь пидора дня?",
                    "{0}, кого ты выберешь пидором?",
                    "{0}, ну что, кого ты выберешь пидором?"),
                new ShortNamePidorText(callerPidor.get())),
            keyboardMarkupBuilder.build(),
            message.getMessageId()));
  }

  @Override
  public Command getCommand() {
    return Command.PIDOR;
  }
}
