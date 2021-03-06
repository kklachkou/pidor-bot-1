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
import by.kobyzau.tg.bot.pbot.service.PidorService;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PidorCleanupChatHandlerTest extends BotActionAbstractTest {

    @InjectMocks
    private CleanupHandler handler = new PidorCleanupChatHandler();
    @Mock
    private Logger logger;
    @Mock
    private TelegramService telegramService;
    @Mock
    private PidorRepository pidorRepository;
    @Mock
    private DailyPidorRepository dailyPidorRepository;
    @Mock
    private PidorService pidorService;
    @Mock
    private DateService dateService;

    private LocalDate now;
    private final long chatId = 123;

    @Before
    public void setUp() {
        now = LocalDate.of(2021, 10, 11);
        doReturn(now).when(dateService).getNow();
        doReturn(Optional.empty()).when(telegramService).getChat(chatId);
        doReturn(Collections.singletonList(chatId)).when(pidorRepository).getChatIdsWithPidors();
    }

    @Test
    public void cleanupTest_noActive() {
        // given
        List<Pidor> pidors = new ArrayList<>();
        pidors.add(getPidor(1, now.minusDays(0)));
        pidors.add(getPidor(2, now.minusDays(1)));
        pidors.add(getPidor(3, now.minusDays(9)));
        pidors.add(getPidor(5, now.minusDays(15)));
        pidors.add(getPidor(6, now.minusDays(16)));
        pidors.add(getPidor(7, now.minusDays(17)));
        pidors.add(getPidor(8, now.minusDays(18)));
        pidors.add(getPidor(9, now.minusDays(15)));
        doReturn(pidors).doReturn(Collections.emptyList()).when(pidorRepository).getByChat(chatId);
        doReturn(Collections.emptyList()).when(pidorService).getByChat(chatId);


        // when
        handler.cleanup();

        // then
        verify(pidorRepository).delete(1);
        verify(pidorRepository).delete(2);
        verify(pidorRepository).delete(3);
        verify(pidorRepository).delete(5);
        verify(pidorRepository).delete(6);
        verify(pidorRepository).delete(7);
        verify(pidorRepository).delete(8);
        verify(pidorRepository).delete(9);
        checkNoAnyActions();
    }


    @Test
    public void cleanupTest_hasActive() {
        // given
        List<Pidor> pidors = new ArrayList<>();
        pidors.add(getPidor(1, now.minusDays(0)));
        pidors.add(getPidor(2, now.minusDays(1)));
        pidors.add(getPidor(3, now.minusDays(9)));
        pidors.add(getPidor(5, now.minusDays(15)));
        pidors.add(getPidor(6, now.minusDays(16)));
        pidors.add(getPidor(7, now.minusDays(17)));
        pidors.add(getPidor(8, now.minusDays(18)));
        pidors.add(getPidor(9, now.minusDays(15)));
        doReturn(pidors).when(pidorRepository).getByChat(chatId);
        doReturn(pidors).when(pidorService).getByChat(chatId);

        // when
        handler.cleanup();

        // then
        verify(pidorRepository, times(0)).delete(1);
        verify(pidorRepository, times(0)).delete(2);
        verify(pidorRepository, times(0)).delete(3);
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
