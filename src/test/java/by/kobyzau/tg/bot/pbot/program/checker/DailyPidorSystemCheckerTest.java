package by.kobyzau.tg.bot.pbot.program.checker;

import by.kobyzau.tg.bot.pbot.RuntimeExecutor;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DailyPidorSystemCheckerTest {

  @InjectMocks private DailyPidorSystemChecker checker = new DailyPidorSystemChecker();

  @Mock private Logger logger;

  @Mock private TelegramService telegramService;

  @Mock private BotService botService;

  @Mock private DailyPidorRepository dailyPidorRepository;

  @Spy private Executor executor = new RuntimeExecutor();

  private final long chatId = 123;

  @Before
  public void init() throws Exception {
    doReturn(Collections.singletonList(chatId)).when(telegramService).getChatIds();
    doReturn(true).when(botService).isChatValid(chatId);
  }

  @Test
  public void check_noDailyPidors() {
    // given
    doReturn(Collections.emptyList()).when(dailyPidorRepository).getByChat(chatId);

    // when
    checker.check();

    // then
    assertWarns(0);
  }

  @Test
  public void check_noBadDaily() {
    // given
    doReturn(
            Arrays.asList(
                getDailyPidor(1, 101, LocalDate.of(2020, 1, 10)),
                getDailyPidor(2, 102, LocalDate.of(2020, 1, 11)),
                getDailyPidor(3, 103, LocalDate.of(2020, 1, 12)),
                getDailyPidor(4, 101, LocalDate.of(2020, 1, 13))))
        .when(dailyPidorRepository)
        .getByChat(chatId);

    // when
    checker.check();

    // then
    assertWarns(0);
  }

  @Test
  public void check_badDailyForSameUser() {
    //given
    doReturn(
            Arrays.asList(
                getDailyPidor(1, 101, LocalDate.of(2020, 1, 10)),
                getDailyPidor(2, 102, LocalDate.of(2020, 1, 11)),
                getDailyPidor(3, 103, LocalDate.of(2020, 1, 12)),
                getDailyPidor(4, 101, LocalDate.of(2020, 1, 10))))
        .when(dailyPidorRepository)
        .getByChat(chatId);

    // when
    checker.check();

    // then
    assertWarns(1);
  }

  @Test
  public void check_badDailyForDifferentUser() {
    //given
    doReturn(
            Arrays.asList(
                getDailyPidor(1, 101, LocalDate.of(2020, 1, 10)),
                getDailyPidor(2, 102, LocalDate.of(2020, 1, 10)),
                getDailyPidor(3, 103, LocalDate.of(2020, 1, 12)),
                getDailyPidor(4, 101, LocalDate.of(2020, 1, 13))))
        .when(dailyPidorRepository)
        .getByChat(chatId);

    // when
    checker.check();

    // then
    assertWarns(1);
  }

  @Test
  public void check_badDailyForMultipleUsers() {
    //given
    doReturn(
            Arrays.asList(
                getDailyPidor(1, 101, LocalDate.of(2020, 1, 10)),
                getDailyPidor(2, 102, LocalDate.of(2020, 1, 10)),
                getDailyPidor(3, 103, LocalDate.of(2020, 1, 10)),
                getDailyPidor(4, 101, LocalDate.of(2020, 1, 13))))
        .when(dailyPidorRepository)
        .getByChat(chatId);

    // when
    checker.check();

    // then
    assertWarns(2);
  }

  private void assertWarns(int times) {
    verify(logger, times(times)).warn(anyString());
  }

  private DailyPidor getDailyPidor(int id, int userId, LocalDate localDate) {
    DailyPidor dailyPidor = new DailyPidor(userId, chatId, localDate);
    dailyPidor.setId(id);
    return dailyPidor;
  }
}
