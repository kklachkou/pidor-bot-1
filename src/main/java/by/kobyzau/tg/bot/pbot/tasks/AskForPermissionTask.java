package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component("askForPermissionTask")
public class AskForPermissionTask implements Task {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private TelegramService telegramService;
  @Autowired private Logger logger;

  @Autowired private BotService botService;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    if (DateUtil.now().getDayOfMonth() != 10) {
      return;
    }
    telegramService
        .getChatIds()
        .forEach(chatId -> executor.execute(() -> askForPermission(chatId)));
  }

  private void askForPermission(long chatId) {
    if (!botService.canPinMessage(chatId)) {
      logger.info("\uD83D\uDCC6 Asking permission of " + chatId);
      botActionCollector.text(
          chatId,
          new SimpleText(
              "Администратору чата привет! Если дать мне права на закрепление сообщений, будет намного удобнее следить за тем, кто сегодня пидор или какая игра сегодня проходит"));
      botActionCollector.text(
          chatId,
          new TextBuilder(
                  new SimpleText(
                      "Если чат приватный, бота также нужно сделать администратором чата:("))
              .append(new NewLineText())
              .append(new NewLineText())
              .append(
                  new ParametizedText(
                      "Так сказал телеграмм: https://core.telegram.org/bots/api#pinchatmessage")));
      botActionCollector.text(
          chatId,
          new SimpleText(
              "Но не переживай, можете не давать администратору никаких прав, кроме права закрепления сообщений"));
    }
  }
}
