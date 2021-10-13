package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.RuntimeExecutor;
import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.helper.BlackBoxHelper;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.checker.BotActionAbstractTest;
import by.kobyzau.tg.bot.pbot.checker.BotTypeBotActionChecker;
import by.kobyzau.tg.bot.pbot.checker.SimpleStickerActionChecker;
import by.kobyzau.tg.bot.pbot.checker.TextBotActionChecker;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.action.WaitBotAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BlackBoxStartTaskTest extends BotActionAbstractTest {

  @Mock private TelegramService telegramService;

  @Mock private UserArtifactService userArtifactService;
  @Mock private Logger logger;
  @Mock private BlackBoxHelper blackBoxHelper;

  @Spy private Executor executor = new RuntimeExecutor();

  @InjectMocks private BlackBoxStartTask task;

  private static final long CHAT_ID = 123;

  @Test
  public void processTask_test() {
    // given
    doReturn(4).when(blackBoxHelper).getNumArtifactsPerDay(CHAT_ID);
    doReturn(Collections.singletonList(CHAT_ID)).when(telegramService).getChatIds();

    // when
    task.processTask();

    // then
    checkActions(
        new BotTypeBotActionChecker(WaitBotAction.class),
        new TextBotActionChecker(
            CHAT_ID,
            new SimpleText(
                "Это <b>черный ящик</b>!" + "\nВнутри лежит артефакт, бонус или анти-бонус")),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new SimpleStickerActionChecker(CHAT_ID, StickerType.GIFT));
    verify(userArtifactService).clearUserArtifacts(CHAT_ID, ArtifactType.PIDOR_MAGNET);
    verify(userArtifactService).clearUserArtifacts(CHAT_ID, ArtifactType.ANTI_PIDOR);
  }
}
