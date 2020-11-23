package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.DiceService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.DicePostActionWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component("diceStart")
public class DiceStartTask implements Task {

  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private DiceService diceService;

  @Autowired private Logger logger;
  @Autowired private BotService botService;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    if (diceService.getGame(DateUtil.now()).isPresent()) {
      logger.info("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
      telegramService.getChatIds().stream()
          .filter(botService::isChatValid)
          .forEach(chatId -> executor.execute(() -> sendDice(chatId)));
    }
  }

  private void sendDice(long chatId) {
    try {
      EmojiGame game = diceService.getGame(DateUtil.now()).orElseThrow(IllegalStateException::new);
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.text(chatId, new SimpleText("Всем здарова!"));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      game.printIntro(chatId);
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new DicePostActionWrapperBotAction(chatId, game, value -> {}),
              botService.canPinMessage(chatId)));
      botActionCollector.text(chatId, new SimpleText("Ну всё! Игра началась!!"));
    } catch (Exception e) {
      logger.error("Cannot start dice for chat " + chatId, e);
    }
  }
}
