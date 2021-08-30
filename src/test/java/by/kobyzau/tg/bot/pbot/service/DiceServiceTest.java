package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGameType;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorDice;
import by.kobyzau.tg.bot.pbot.repository.dice.DiceRepository;
import by.kobyzau.tg.bot.pbot.service.impl.DiceServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DiceServiceTest {

  private final long chatId = 1;
  @Mock private DiceRepository diceRepository;

  @Mock private PidorService pidorService;
  @Mock private CalendarSchedule calendarSchedule;
  @InjectMocks private DiceService service = new DiceServiceImpl();

  @Test
  public void getDices_test() {
    // given
    long userId = 30;
    LocalDate date = LocalDate.of(2021, 8, 29);
    doReturn(
            Arrays.asList(
                getDice(1, userId - 1, date),
                getDice(2, userId, date),
                getDice(3, userId + 1, date.plusDays(1))))
        .when(diceRepository)
        .getByChatId(chatId);

    // when
    List<PidorDice> dices = service.getDices(chatId, date);

    // then
    assertEquals(Arrays.asList(getDice(1, userId - 1, date), getDice(2, userId, date)), dices);
  }

  @Test
  public void getUserDice_test() {
    // given
    long userId = 30;
    LocalDate date = LocalDate.of(2021, 8, 29);
    doReturn(
            Arrays.asList(
                getDice(1, userId - 1, date),
                getDice(2, userId, date),
                getDice(3, userId + 1, date.plusDays(1))))
        .when(diceRepository)
        .getByChatId(chatId);

    // when
    Optional<PidorDice> dice = service.getUserDice(chatId, userId, date);

    // then
    assertTrue(dice.isPresent());
    assertEquals(getDice(2, userId, date), dice.get());
  }

  @Test
  public void getGame_noSchedule() {
    // given
    LocalDate date = LocalDate.of(2021, 8, 29);
    doReturn(ScheduledItem.NONE).when(calendarSchedule).getItem(chatId, date);

    // when
    Optional<EmojiGame> game = service.getGame(chatId, date);

    // then
    assertFalse(game.isPresent());
  }

  @Test
  public void getGame_hasDated() {
    // given
    LocalDate date = LocalDate.of(2021, 8, 29);
    doReturn(ScheduledItem.EMOJI_GAME).when(calendarSchedule).getItem(chatId, date);
    ReflectionTestUtils.setField(
        service, "games", Collections.singletonList(getGame(EmojiGameType.FOOTBALL, date)));

    // when
    Optional<EmojiGame> game = service.getGame(chatId, date);

    // then
    assertTrue(game.isPresent());
    assertEquals(EmojiGameType.FOOTBALL, game.get().getType());
  }

  @Test
  public void getGame_noDated_withDiceBackup() {
    // given
    LocalDate date = LocalDate.of(2021, 8, 29);
    doReturn(ScheduledItem.EMOJI_GAME).when(calendarSchedule).getItem(chatId, date);
    ReflectionTestUtils.setField(
        service,
        "games",
        Arrays.asList(
            getGame(EmojiGameType.FOOTBALL, date.plusDays(1)),
            getGame(EmojiGameType.DICE, date.plusDays(1)),
            getGame(EmojiGameType.FOOTBALL, date.minusDays(1))));

    // when
    Optional<EmojiGame> game = service.getGame(chatId, date);

    // then
    assertTrue(game.isPresent());
    assertEquals(EmojiGameType.DICE, game.get().getType());
  }

  @Test
  public void getGame_noDated_withoutDiceBackup() {
    // given
    LocalDate date = LocalDate.of(2021, 8, 29);
    doReturn(ScheduledItem.EMOJI_GAME).when(calendarSchedule).getItem(chatId, date);
    ReflectionTestUtils.setField(
        service,
        "games",
        Arrays.asList(
            getGame(EmojiGameType.FOOTBALL, date.plusDays(1)),
            getGame(EmojiGameType.FOOTBALL, date.minusDays(1))));

    // when
    Optional<EmojiGame> game = service.getGame(chatId, date);

    // then
    assertFalse(game.isPresent());
  }

  @Test
  public void getNumPidorsToPlay_firstNumbers() {
    Map<Integer, Integer> countToResult = new HashMap<>();
    countToResult.put(0, 0);
    countToResult.put(1, 1);
    countToResult.put(2, 2);
    countToResult.put(3, 3);
    countToResult.put(4, 4);
    countToResult.put(5, 5);
    countToResult.put(6, 5);
    countToResult.put(7, 5);
    countToResult.put(8, 6);
    countToResult.put(9, 7);
    countToResult.put(10, 8);
    countToResult.put(100, 80);

    for (Map.Entry<Integer, Integer> data : countToResult.entrySet()) {
      doReturn(
              IntStream.range(0, data.getKey())
                  .mapToObj(i -> new Pidor())
                  .collect(Collectors.toList()))
          .when(pidorService)
          .getByChat(chatId);
      Integer numPidorsToPlay = service.getNumPidorsToPlay(chatId);
      assertEquals(data.getValue(), numPidorsToPlay);
    }
  }

  private EmojiGame getGame(EmojiGameType type, LocalDate date) {
    EmojiGame game = mock(EmojiGame.class, "EmojiGame:" + type);
    doReturn(type).when(game).getType();
    doReturn(true).when(game).isDateToGame(date);
    return game;
  }

  private PidorDice getDice(long id, long userId, LocalDate date) {
    return PidorDice.builder().id(id).chatId(chatId).playerTgId(userId).localDate(date).build();
  }
}
