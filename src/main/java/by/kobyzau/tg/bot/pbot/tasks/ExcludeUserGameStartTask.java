package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.ExcludeGameService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import java.time.LocalDate;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Component("excludeUserGameStartTask")
public class ExcludeUserGameStartTask implements Task {

  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private ExcludeGameService gameService;

  @Autowired private Logger logger;

  @Autowired private BotService botService;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    LocalDate now = DateUtil.now();
    telegramService.getChatIds().stream()
        .filter(botService::isChatValid)
        .filter(chatId -> gameService.isExcludeGameDay(chatId, now))
        .forEach(chatId -> executor.execute(() -> sendInfo(chatId)));
  }

  private void sendInfo(long chatId) {
    try {
      int numPidorsToExclude = gameService.getNumPidorsToExclude(chatId);
      botActionCollector.wait(chatId, ChatAction.TYPING);
      String wordOfTheDay = gameService.getWordOfTheDay(DateUtil.now());
      botActionCollector.text(chatId, new SimpleText("Всем здарова!"));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.text(
          chatId, new SimpleText("Сегодня пидором будет самая медленная рука Дикого Запада"));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.text(
          chatId,
          new ParametizedText(
              "Напиши <i><b>{0}</b></i> и ты не будешь сегодня пидором!",
              new SimpleText(wordOfTheDay)));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.text(
          chatId,
          new ParametizedText(
              "Но поспеши, ведь я исключу из игры лишь {0}х", new IntText(numPidorsToExclude)));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      KeyboardRow replyButton = new KeyboardRow();
      replyButton.add(wordOfTheDay);
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new SendMessageBotAction(chatId, new SimpleText(wordOfTheDay))
                  .withReplyMarkup(
                      ReplyKeyboardMarkup.builder()
                          .resizeKeyboard(true)
                          .oneTimeKeyboard(true)
                          .keyboardRow(replyButton)
                          .build()),
              botService.canPinMessage(chatId)));
    } catch (Exception e) {
      logger.error("Cannot start exclude user game for chat " + chatId, e);
    }
  }
}
