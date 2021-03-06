package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorMark;
import by.kobyzau.tg.bot.pbot.model.PidorOfYear;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidorofyear.PidorOfYearRepository;
import by.kobyzau.tg.bot.pbot.service.impl.PidorServiceImpl;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class PidorServiceTest {

  @Mock private PidorRepository pidorRepository;
  @Mock private PidorOfYearRepository pidorOfYearRepository;
  @Mock private DailyPidorRepository dailyPidorRepository;
  @Mock private TelegramService telegramService;
  @Mock private UserArtifactService userArtifactService;

  @InjectMocks private PidorService pidorService = new PidorServiceImpl();

  private final long chatId = 123;
  private final long userId = 111;

  @Before
  public void init() {
    doReturn(
            Arrays.asList(
                new PidorOfYear(userId, chatId, 2010),
                new PidorOfYear(userId + 1, chatId, DateUtil.now().getYear() - 1)))
        .when(pidorOfYearRepository)
        .getPidorOfYearByChat(chatId);
    doReturn(
            Arrays.asList(
                new DailyPidor(userId, chatId, LocalDate.of(2020, 7, 7)),
                new DailyPidor(userId + 1, chatId, LocalDate.of(2020, 10, 10)),
                new DailyPidor(userId, chatId, LocalDate.of(2020, 4, 4))))
        .when(dailyPidorRepository)
        .getByChat(chatId);
  }

  @Test
  public void getPidor_noPidors_empty() {
    // given
    doReturn(Optional.empty()).when(pidorRepository).getByChatAndPlayerTgId(chatId, userId);

    // when
    Optional<Pidor> pidor = pidorService.getPidor(chatId, userId);

    // then
    assertFalse(pidor.isPresent());
  }

  @Test
  public void getPidor_simplePidor() {
    // given
    doReturn(Optional.of(new Pidor(userId, chatId, "Pidor " + userId)))
        .when(pidorRepository)
        .getByChatAndPlayerTgId(chatId, userId);
    ChatMember chatMember = mock(ChatMember.class);
    doReturn("member").when(chatMember).getStatus();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(chatId, userId);

    // when
    Optional<Pidor> pidor = pidorService.getPidor(chatId, userId);

    // then
    assertTrue(pidor.isPresent());
    assertEquals(userId, pidor.get().getTgId());
    assertEquals(chatId, pidor.get().getChatId());
    assertEquals(Collections.emptyList(), pidor.get().getPidorMarks());
    assertEquals(Collections.emptySet(), pidor.get().getArtifacts());
  }

  @Test
  public void getPidor_pidorOfYear() {
    // given
    doReturn(Optional.of(new Pidor(userId, chatId, "Pidor " + userId)))
        .when(pidorRepository)
        .getByChatAndPlayerTgId(chatId, userId);
    ChatMember chatMember = mock(ChatMember.class);
    doReturn("member").when(chatMember).getStatus();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(chatId, userId);
    doReturn(
            Arrays.asList(
                new PidorOfYear(userId, chatId, DateUtil.now().getYear() - 1),
                new PidorOfYear(userId + 1, chatId, DateUtil.now().getYear() - 2)))
        .when(pidorOfYearRepository)
        .getPidorOfYearByChat(chatId);

    // when
    Optional<Pidor> pidor = pidorService.getPidor(chatId, userId);

    // then
    assertTrue(pidor.isPresent());
    assertEquals(userId, pidor.get().getTgId());
    assertEquals(chatId, pidor.get().getChatId());
    assertEquals(Collections.singletonList(PidorMark.PIDOR_OF_YEAR), pidor.get().getPidorMarks());
    assertEquals(Collections.emptySet(), pidor.get().getArtifacts());
  }

  @Test
  public void getPidor_pidorOfCurrentYear() {
    // given
    doReturn(Optional.of(new Pidor(userId, chatId, "Pidor " + userId)))
        .when(pidorRepository)
        .getByChatAndPlayerTgId(chatId, userId);
    ChatMember chatMember = mock(ChatMember.class);
    doReturn("member").when(chatMember).getStatus();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(chatId, userId);
    doReturn(
            Arrays.asList(
                new PidorOfYear(userId, chatId, DateUtil.now().getYear()),
                new PidorOfYear(userId + 1, chatId, DateUtil.now().getYear() - 1)))
        .when(pidorOfYearRepository)
        .getPidorOfYearByChat(chatId);

    // when
    Optional<Pidor> pidor = pidorService.getPidor(chatId, userId);

    // then
    assertTrue(pidor.isPresent());
    assertEquals(userId, pidor.get().getTgId());
    assertEquals(chatId, pidor.get().getChatId());
    assertEquals(Collections.singletonList(PidorMark.PIDOR_OF_YEAR), pidor.get().getPidorMarks());
    assertEquals(Collections.emptySet(), pidor.get().getArtifacts());
  }

  @Test
  public void getPidor_pidorOfDay() {
    // given
    doReturn(Optional.of(new Pidor(userId, chatId, "Pidor " + userId)))
        .when(pidorRepository)
        .getByChatAndPlayerTgId(chatId, userId);
    ChatMember chatMember = mock(ChatMember.class);
    doReturn("member").when(chatMember).getStatus();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(chatId, userId);
    doReturn(
            Arrays.asList(
                new DailyPidor(userId, chatId, LocalDate.of(2020, 7, 7)),
                new DailyPidor(userId + 1, chatId, LocalDate.of(2020, 10, 10)),
                new DailyPidor(userId, chatId, LocalDate.of(2020, 11, 11)),
                new DailyPidor(userId, chatId, LocalDate.of(2020, 4, 4))))
        .when(dailyPidorRepository)
        .getByChat(chatId);

    // when
    Optional<Pidor> pidor = pidorService.getPidor(chatId, userId);

    // then
    assertTrue(pidor.isPresent());
    assertEquals(userId, pidor.get().getTgId());
    assertEquals(chatId, pidor.get().getChatId());
    assertEquals(
        Collections.singletonList(PidorMark.LAST_PIDOR_OF_DAY), pidor.get().getPidorMarks());
    assertEquals(Collections.emptySet(), pidor.get().getArtifacts());
  }

  @Test
  public void getPidor_pidorOfDayAndYear() {
    // given
    doReturn(Optional.of(new Pidor(userId, chatId, "Pidor " + userId)))
        .when(pidorRepository)
        .getByChatAndPlayerTgId(chatId, userId);
    ChatMember chatMember = mock(ChatMember.class);
    doReturn("member").when(chatMember).getStatus();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(chatId, userId);
    doReturn(
            Arrays.asList(
                new DailyPidor(userId, chatId, LocalDate.of(2020, 7, 7)),
                new DailyPidor(userId + 1, chatId, LocalDate.of(2020, 10, 10)),
                new DailyPidor(userId, chatId, LocalDate.of(2020, 11, 11)),
                new DailyPidor(userId, chatId, LocalDate.of(2020, 4, 4))))
        .when(dailyPidorRepository)
        .getByChat(chatId);
    doReturn(
            Arrays.asList(
                new PidorOfYear(userId, chatId, DateUtil.now().getYear() - 1),
                new PidorOfYear(userId + 1, chatId, DateUtil.now().getYear() - 2)))
        .when(pidorOfYearRepository)
        .getPidorOfYearByChat(chatId);

    // when
    Optional<Pidor> pidor = pidorService.getPidor(chatId, userId);

    // then
    assertTrue(pidor.isPresent());
    assertEquals(userId, pidor.get().getTgId());
    assertEquals(chatId, pidor.get().getChatId());
    assertEquals(
        Arrays.asList(PidorMark.PIDOR_OF_YEAR, PidorMark.LAST_PIDOR_OF_DAY),
        pidor.get().getPidorMarks());
    assertEquals(Collections.emptySet(), pidor.get().getArtifacts());
  }

  @Test
  public void getPidor_withArtifact() {
    // given
    doReturn(Optional.of(new Pidor(userId, chatId, "Pidor " + userId)))
        .when(pidorRepository)
        .getByChatAndPlayerTgId(chatId, userId);
    ChatMember chatMember = mock(ChatMember.class);
    doReturn("member").when(chatMember).getStatus();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(chatId, userId);
    doReturn(Collections.emptyList()).when(dailyPidorRepository).getByChat(chatId);
    doReturn(Collections.emptyList()).when(pidorOfYearRepository).getPidorOfYearByChat(chatId);
    doReturn(
            Collections.singletonList(
                UserArtifact.builder().artifactType(ArtifactType.PIDOR_MAGNET).build()))
        .when(userArtifactService)
        .getUserArtifacts(chatId, userId);

    // when
    Optional<Pidor> pidor = pidorService.getPidor(chatId, userId);

    // then
    assertTrue(pidor.isPresent());
    assertEquals(userId, pidor.get().getTgId());
    assertEquals(chatId, pidor.get().getChatId());
    assertEquals(Collections.emptyList(), pidor.get().getPidorMarks());
    assertEquals(Collections.singleton(ArtifactType.PIDOR_MAGNET), pidor.get().getArtifacts());
  }

  @Test
  public void getPidor_pidorOfDayAndYear_withArtifacts() {
    // given
    doReturn(Optional.of(new Pidor(userId, chatId, "Pidor " + userId)))
        .when(pidorRepository)
        .getByChatAndPlayerTgId(chatId, userId);
    ChatMember chatMember = mock(ChatMember.class);
    doReturn("member").when(chatMember).getStatus();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(chatId, userId);
    doReturn(
            Arrays.asList(
                new DailyPidor(userId, chatId, LocalDate.of(2020, 7, 7)),
                new DailyPidor(userId + 1, chatId, LocalDate.of(2020, 10, 10)),
                new DailyPidor(userId, chatId, LocalDate.of(2020, 11, 11)),
                new DailyPidor(userId, chatId, LocalDate.of(2020, 4, 4))))
        .when(dailyPidorRepository)
        .getByChat(chatId);
    doReturn(
            Arrays.asList(
                new PidorOfYear(userId, chatId, DateUtil.now().getYear() - 1),
                new PidorOfYear(userId + 1, chatId, DateUtil.now().getYear() - 2)))
        .when(pidorOfYearRepository)
        .getPidorOfYearByChat(chatId);
    doReturn(
            Collections.singletonList(
                UserArtifact.builder().artifactType(ArtifactType.PIDOR_MAGNET).build()))
        .when(userArtifactService)
        .getUserArtifacts(chatId, userId);

    // when
    Optional<Pidor> pidor = pidorService.getPidor(chatId, userId);

    // then
    assertTrue(pidor.isPresent());
    assertEquals(userId, pidor.get().getTgId());
    assertEquals(chatId, pidor.get().getChatId());
    assertEquals(Collections.singleton(ArtifactType.PIDOR_MAGNET), pidor.get().getArtifacts());
    assertEquals(
        Arrays.asList(PidorMark.PIDOR_OF_YEAR, PidorMark.LAST_PIDOR_OF_DAY),
        pidor.get().getPidorMarks());
  }
}
