package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.HotPotatoesService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.Executor;

@Component("hotPotatoStartTask")
public class HotPotatoStartTask implements Task {
  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private HotPotatoesService hotPotatoesService;

  @Autowired private Logger logger;

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

    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(
        chatId, new RandomText("Всем здарова!", "Алоха!", "Всем привет!", "Вечер в хату!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.add(
        new PingMessageWrapperBotAction(
            new SendMessageBotAction(
                chatId,
                new SimpleText(
                    "Сегодня день <b>горячей картошечки</b>\uD83E\uDD54\uD83D\uDD25!\n"
                        + "Перекидывай картошечку с пидора на пидора, пока она не сгорит!\n"
                        + "Жми /pidor чтобы закинуть картошечку в чат!"))));
  }
}
