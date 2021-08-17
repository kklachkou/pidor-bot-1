package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
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

import java.time.LocalDate;
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
    LocalDate now = DateUtil.now();
    logger.debug("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
    telegramService.getChatIds().stream()
            .filter(chatId -> diceService.getGame(chatId, now).isPresent())
            .forEach(chatId -> executor.execute(() -> sendDice(chatId)));
  }

  private void sendDice(long chatId) {
    EmojiGame game = diceService.getGame(chatId, DateUtil.now()).orElseThrow(IllegalStateException::new);
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.text(chatId, new RandomText("Всем здарова!", "Алоха!", "Всем привет!", "Вечер в хату!"));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    game.printIntro(chatId);
    botActionCollector.add(
            new PingMessageWrapperBotAction(
                    new DicePostActionWrapperBotAction(chatId, game.getType(), value -> {}),
                    botService.canPinMessage(chatId)));
    botActionCollector.text(chatId, new SimpleText("Ну всё! Игра началась!!"));
  }
}
