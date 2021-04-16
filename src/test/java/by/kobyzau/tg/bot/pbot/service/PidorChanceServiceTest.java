package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.impl.PidorChanceServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class PidorChanceServiceTest {

  @Mock private DailyPidorRepository dailyPidorRepository;
  @Mock private PidorService pidorService;

  @Mock private TelegramService telegramService;

  @Mock private Supplier<LocalDate> nowProvider;
  @Mock private BotActionCollector botActionCollector;

  private PidorChanceService service;

  private long chatId = 10;
  private int year = 2019;
  private int p1 = 1;
  private int p2 = 2;
  private int p3 = 3;
  private int p4 = 4;

  @Before
  public void init() {

    doReturn(Optional.of(new User())).when(telegramService).getChatMember(chatId, p1);
    doReturn(Optional.of(new User())).when(telegramService).getChatMember(chatId, p2);
    doReturn(Optional.of(new User())).when(telegramService).getChatMember(chatId, p3);
    doReturn(Optional.of(new User())).when(telegramService).getChatMember(chatId, p4);
    doReturn(LocalDate.of(year, 1, 1)).when(nowProvider).get();

    service =
        new PidorChanceServiceImpl(
            pidorService,
            dailyPidorRepository,
            botActionCollector,
            nowProvider,
            1000);
  }

  @Test(timeout = 20000)
  public void calcChances_withoutDaily() {
    doReturn(LocalDate.of(year, 1, 31)).when(nowProvider).get();
    doReturn(Collections.emptyList()).when(dailyPidorRepository).getByChat(chatId);
    doReturn(
            Arrays.asList(
                getPidor(p1, chatId),
                getPidor(p2, chatId),
                getPidor(p3, chatId),
                getPidor(p4, chatId)))
        .when(pidorService)
        .getByChat(chatId);

    List<Pair<Pidor, Double>> pairs = service.calcChances(chatId, year);

    for (Pair<Pidor, Double> pair : pairs) {
      assertTrue(
          "calcChances_withoutDaily (20-30) - "
              + pair.getLeft().getFullName()
              + " "
              + pair.getRight(),
          pair.getRight() > 20 && pair.getRight() < 30);
    }
  }

  @Test(timeout = 10000)
  public void calcChances_withDaily_past() {
    doReturn(LocalDate.of(year, 12, 30)).when(nowProvider).get();
    doReturn(Collections.singletonList(getDailyPidor(p2, LocalDate.of(year, 12, 20))))
        .when(dailyPidorRepository)
        .getByChat(chatId);
    doReturn(
            Arrays.asList(
                getPidor(p1, chatId),
                getPidor(p2, chatId),
                getPidor(p3, chatId),
                getPidor(p4, chatId)))
        .when(pidorService)
        .getByChat(chatId);

    List<Pair<Pidor, Double>> pairs = service.calcChances(chatId, year);

    for (Pair<Pidor, Double> pair : pairs) {
      if (pair.getLeft().getTgId() == p2) {
        assertTrue(
            "calcChances_withDaily_past (30-60) - "
                + pair.getLeft().getFullName()
                + " "
                + pair.getRight(),
            pair.getRight() > 30 && pair.getRight() < 60);
      } else {
        assertTrue(
            "calcChances_withDaily_past (10-25) - "
                + pair.getLeft().getFullName()
                + " "
                + pair.getRight(),
            pair.getRight() > 10 && pair.getRight() < 25);
      }
    }
  }

  @Test(timeout = 10000)
  public void calcChances_withDaily_today() {
    LocalDate today = LocalDate.of(year, 12, 29);
    doReturn(today).when(nowProvider).get();
    doReturn(Collections.singletonList(getDailyPidor(p2, today)))
        .when(dailyPidorRepository)
        .getByChat(chatId);
    doReturn(
            Arrays.asList(
                getPidor(p1, chatId),
                getPidor(p2, chatId),
                getPidor(p3, chatId),
                getPidor(p4, chatId)))
        .when(pidorService)
        .getByChat(chatId);

    List<Pair<Pidor, Double>> pairs = service.calcChances(chatId, year);

    for (Pair<Pidor, Double> pair : pairs) {
      if (pair.getLeft().getTgId() == p2) {
        assertTrue(
            "calcChances_withDaily_past (30-60) - "
                + pair.getLeft().getFullName()
                + " "
                + pair.getRight(),
            pair.getRight() > 30 && pair.getRight() < 60);
      } else {
        assertTrue(
            "calcChances_withDaily_past (10-25) - "
                + pair.getLeft().getFullName()
                + " "
                + pair.getRight(),
            pair.getRight() > 10 && pair.getRight() < 25);
      }
    }
  }

  private Pidor getPidor(int id, long chatId) {
    return new Pidor(id, chatId, id + "");
  }

  private DailyPidor getDailyPidor(long tgId, LocalDate localDate) {
    DailyPidor dailyPidor = new DailyPidor();
    dailyPidor.setChatId(chatId);
    dailyPidor.setLocalDate(localDate);
    dailyPidor.setPlayerTgId(tgId);
    return dailyPidor;
  }
}
