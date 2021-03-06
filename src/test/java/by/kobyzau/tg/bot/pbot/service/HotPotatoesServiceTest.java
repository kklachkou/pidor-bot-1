package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.HotPotatoTaker;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.potato.PotatoTakerRepository;
import by.kobyzau.tg.bot.pbot.service.impl.HotPotatoesServiceImpl;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.HotPotatoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HotPotatoesServiceTest {

  @Mock private PotatoTakerRepository potatoTakerRepository;
  @Mock private PidorService pidorService;

  @Mock private UserArtifactService userArtifactService;
  @Mock private CalendarSchedule calendarSchedule;
  @Mock private HotPotatoUtil hotPotatoUtil;
  @InjectMocks private HotPotatoesService service = new HotPotatoesServiceImpl();

  private static final int CHAT_ID = 1;
  private static final int USER_ID = 2;

  @Test
  public void isHotPotatoesDay_potato() {
    // given
    ScheduledItem item = ScheduledItem.POTATOES;
    LocalDate date = DateUtil.now();
    doReturn(item).when(calendarSchedule).getItem(CHAT_ID, date);

    // when
    boolean hotPotatoesDay = service.isHotPotatoesDay(CHAT_ID, date);

    // then
    assertTrue(hotPotatoesDay);
  }

  @Test
  public void isHotPotatoesDay_another() {
    // given
    ScheduledItem item = ScheduledItem.NONE;
    LocalDate date = DateUtil.now();
    doReturn(item).when(calendarSchedule).getItem(CHAT_ID, date);

    // when
    boolean hotPotatoesDay = service.isHotPotatoesDay(CHAT_ID, date);

    // then
    assertFalse(hotPotatoesDay);
  }

  @Test
  public void getLastTaker_noTakers() {
    // given
    LocalDate date = DateUtil.now();
    doReturn(Collections.emptyList()).when(potatoTakerRepository).getByChatAndDate(CHAT_ID, date);

    // when
    Optional<Pidor> lastTaker = service.getLastTaker(date, CHAT_ID);

    // then
    assertFalse(lastTaker.isPresent());
  }

  @Test
  public void getLastTaker_noPidor() {
    // given
    LocalDate date = DateUtil.now();
    doReturn(Collections.singletonList(taker(10, USER_ID, date, 0)))
        .when(potatoTakerRepository)
        .getByChatAndDate(CHAT_ID, date);
    doReturn(Optional.empty()).when(pidorService).getPidor(CHAT_ID, USER_ID);

    // when
    Optional<Pidor> lastTaker = service.getLastTaker(date, CHAT_ID);

    // then
    assertFalse(lastTaker.isPresent());
  }

  @Test
  public void getLastTaker_multipleTakers() {
    // given
    LocalDate date = DateUtil.now();
    doReturn(
            Arrays.asList(
                taker(100, 0, date, 10),
                taker(8, 0, date, 15),
                taker(101, USER_ID, date, 15),
                taker(9, 0, date, 15),
                taker(0, 0, date, 13)))
        .when(potatoTakerRepository)
        .getByChatAndDate(CHAT_ID, date);
    doReturn(Optional.of(new Pidor(USER_ID, CHAT_ID, "Pidor" + USER_ID)))
        .when(pidorService)
        .getPidor(CHAT_ID, USER_ID);

    // when
    Optional<Pidor> lastTaker = service.getLastTaker(date, CHAT_ID);

    // then
    assertTrue(lastTaker.isPresent());
    assertEquals(USER_ID, lastTaker.get().getTgId());
    assertEquals(CHAT_ID, lastTaker.get().getChatId());
  }

  @Test
  public void getLastTakerDeadline_noTakers() {
    // given
    LocalDate date = DateUtil.now();
    doReturn(Collections.emptyList()).when(potatoTakerRepository).getByChatAndDate(CHAT_ID, date);

    // when
    Optional<LocalDateTime> deadline = service.getLastTakerDeadline(date, CHAT_ID);

    // then
    assertFalse(deadline.isPresent());
  }

  @Test
  public void getLastTakerDeadline_multipleTakers() {
    // given
    LocalDate date = DateUtil.now();
    doReturn(
            Arrays.asList(
                taker(100, 0, date, 10),
                taker(8, 0, date, 15),
                taker(101, USER_ID, date, 15),
                taker(9, 0, date, 15),
                taker(90, 0, date, 11),
                taker(0, 0, date, 13)))
        .when(potatoTakerRepository)
        .getByChatAndDate(CHAT_ID, date);

    // when
    Optional<LocalDateTime> deadline = service.getLastTakerDeadline(date, CHAT_ID);

    // then
    assertTrue(deadline.isPresent());
    assertEquals(
        LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 15, 0),
        deadline.get());
  }

  @Test
  public void setNewTaker_noArtifacts() {
    // given
    Pidor pidor = new Pidor(USER_ID, CHAT_ID, "FullName");
    LocalDateTime time = DateUtil.currentTime();
    doReturn(Optional.empty())
        .when(userArtifactService)
        .getUserArtifact(CHAT_ID, USER_ID, ArtifactType.HELL_FIRE);
    doReturn(time).when(hotPotatoUtil).getDeadline(any(), eq(HotPotatoUtil.DEFAULT_DIVIDER));

    // when
    LocalDateTime deadline = service.setNewTaker(pidor);

    // then
    assertEquals(time, deadline);
    verify(potatoTakerRepository, times(1))
        .create(new HotPotatoTaker(USER_ID, CHAT_ID, DateUtil.now(), time));
  }

  @Test
  public void setNewTaker_hellFireArtifact() {
    // given
    Pidor pidor = new Pidor(USER_ID, CHAT_ID, "FullName");
    LocalDateTime time = DateUtil.currentTime();
    doReturn(Optional.of(new UserArtifact()))
        .when(userArtifactService)
        .getUserArtifact(CHAT_ID, USER_ID, ArtifactType.HELL_FIRE);
    doReturn(time).when(hotPotatoUtil).getDeadline(any(), eq(HotPotatoUtil.DEFAULT_DIVIDER + 1));

    // when
    LocalDateTime deadline = service.setNewTaker(pidor);

    // then
    assertEquals(time, deadline);
    verify(potatoTakerRepository, times(1))
        .create(new HotPotatoTaker(USER_ID, CHAT_ID, DateUtil.now(), time));
  }

  private HotPotatoTaker taker(long id, long userId, LocalDate date, int hour) {
    HotPotatoTaker taker = new HotPotatoTaker();
    taker.setId(id);
    taker.setPlayerTgId(userId);
    taker.setDate(date);
    taker.setChatId(CHAT_ID);
    taker.setDeadline(
        LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), hour, 0));
    return taker;
  }
}
