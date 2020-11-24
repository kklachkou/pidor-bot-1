package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component("assassinTask")
public class AssassinTask implements Task {

  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private CalendarSchedule calendarSchedule;

  @Autowired private Logger logger;
  @Autowired private BotService botService;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Value("${bot.username}")
  private String botUserName;

  @Override
  public void processTask() {
    if (calendarSchedule.getItem(DateUtil.now()) == ScheduledItem.ASSASSIN) {
      logger.info("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
      telegramService.getChatIds().stream()
          .filter(botService::isChatValid)
          .forEach(chatId -> executor.execute(() -> sendInfo(chatId)));
    }
  }

  private void sendInfo(long chatId) {
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new RandomText("Внимание!", "Всем здарова!", "Вечер в хату!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.add(
        new PingMessageWrapperBotAction(
            new SendMessageBotAction(chatId, new SimpleText("Сегодня день отсасина!")),
            botService.canPinMessage(chatId)));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new SimpleText(
            "Сегодня любой из вас сможет заказать услуги отсасина, чтобы он сделал любого человека в этом чате пидором дня"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "Тебе нужно вызвать команду /{0}, чтобы получить список людей, кого ты можешь сделать пидором.",
            new SimpleText(Command.PIDOR.getName())));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new SimpleText(
            "Однако не всё так просто!\nЕсли закажешь кого-либо, я кину кубик, и если:"
                + "\n\n-выпадет 1-3 - то ничего не произойдёт"
                + "\n\n-выпадет 4-5 - то указанный тобою человек станет пидором дня"
                + "\n\n-выпадет 6 - ты сам окажешься пидором"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "Список личный, не кликай на имена, отправленные другому человеку",
            new SimpleText(Command.PIDOR.getName())));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new SimpleText("Рискнёшь ли ты, чтобы сделать другого пидором?"));
    botActionCollector.wait(chatId, 2, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.LOVE.getRandom());
  }
}
