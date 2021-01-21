package by.kobyzau.tg.bot.pbot.program.cleanup;

import by.kobyzau.tg.bot.pbot.checker.BotActionAbstractTest;
import by.kobyzau.tg.bot.pbot.checker.BotTypeBotActionChecker;
import by.kobyzau.tg.bot.pbot.checker.ContainsTextBotActionChecker;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.cleanup.impl.PidorCleanupChatHandler;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNameLinkedPidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.DateService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.action.WaitBotAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PidorCleanupChatHandlerTest extends BotActionAbstractTest {

  @InjectMocks private CleanupChatHandler handler = new PidorCleanupChatHandler();
  @Mock private Logger logger;
  @Mock private TelegramService telegramService;
  @Mock private PidorRepository pidorRepository;
  @Mock private DailyPidorRepository dailyPidorRepository;
  @Mock private DateService dateService;

  private LocalDate now;
  private final long chatId = 123;

  @Before
  public void setUp() {
    now = LocalDate.of(2021, 10, 11);
    doReturn(now).when(dateService).getNow();
  }

  @Test
  public void cleanupTest() {
    // given
    List<Pidor> pidors = new ArrayList<>();
    pidors.add(getPidor(1, now.minusDays(0)));
    pidors.add(getPidor(2, now.minusDays(1)));
    pidors.add(getPidor(3, now.minusDays(9)));
    pidors.add(getPidor(5, now.minusDays(10)));
    pidors.add(getPidor(6, now.minusDays(11)));
    pidors.add(getPidor(7, now.minusDays(12)));
    pidors.add(getPidor(8, now.minusDays(13)));
    pidors.add(getPidor(9, now.minusDays(10)));
    doReturn(pidors).when(pidorRepository).getByChat(chatId);

    // when
    handler.cleanup(chatId);

    // then
    verify(pidorRepository, times(0)).delete(1);
    verify(pidorRepository, times(0)).delete(2);
    verify(pidorRepository, times(0)).delete(3);
    verify(pidorRepository, times(0)).delete(4);
    verify(pidorRepository, times(0)).delete(5);
    verify(pidorRepository, times(0)).delete(6);
    verify(pidorRepository, times(1)).delete(7);
    verify(pidorRepository, times(1)).delete(8);
    verify(pidorRepository, times(0)).delete(9);
    checkActions(
        new BotTypeBotActionChecker(WaitBotAction.class),
        new ContainsTextBotActionChecker(new ShortNameLinkedPidorText(getPidor(5)).text()),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new ContainsTextBotActionChecker(new ShortNameLinkedPidorText(getPidor(9)).text()));
  }

  private Pidor getPidor(int id) {
    Pidor pidor = new Pidor(id, chatId, "Pidor" + id);
    pidor.setId(id);
    return pidor;
  }

  private Pidor getPidor(int id, LocalDate updated) {
    Pidor pidor = getPidor(id);
    pidor.setUsernameLastUpdated(updated);
    return pidor;
  }
}
