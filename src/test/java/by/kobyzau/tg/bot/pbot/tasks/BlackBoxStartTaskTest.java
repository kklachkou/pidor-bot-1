package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.RuntimeExecutor;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.checker.BotActionAbstractTest;
import by.kobyzau.tg.bot.pbot.checker.SimpleStickerActionChecker;
import by.kobyzau.tg.bot.pbot.checker.TextBotActionChecker;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.helper.DateHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.Executor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BlackBoxStartTaskTest extends BotActionAbstractTest {

  @Mock private TelegramService telegramService;

  @Mock private UserArtifactService userArtifactService;
  @Mock private DateHelper dateHelper;
  @Mock private Logger logger;

  @Spy private Executor executor = new RuntimeExecutor();

  @InjectMocks private BlackBoxStartTask task;

  private static final long CHAT_ID = 123;

  @Test
  public void processTask_anotherDay() {
    LocalDate date = LocalDate.of(2021, 9, 5);
    for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
      date = date.plusDays(1);
      if (dayOfWeek == DayOfWeek.SUNDAY
          || dayOfWeek == DayOfWeek.WEDNESDAY
          || dayOfWeek == DayOfWeek.SATURDAY) {
        continue;
      }
      // given
      doReturn(date).when(dateHelper).now();
      doReturn(Collections.singletonList(CHAT_ID)).when(telegramService).getChatIds();

      // when
      task.processTask();

      // then
      assertEquals(dayOfWeek, dateHelper.now().getDayOfWeek());
      checkNoAnyActions();
    }
  }

  @Test
  public void processTask_sunday() {
    // given
    doReturn(LocalDate.of(2021, 9, 19)).when(dateHelper).now();
    doReturn(Collections.singletonList(CHAT_ID)).when(telegramService).getChatIds();

    // when
    task.processTask();

    // then
    assertEquals(DayOfWeek.SUNDAY, dateHelper.now().getDayOfWeek());
    checkActions(
        new TextBotActionChecker(
            CHAT_ID,
            new SimpleText(
                "Это <b>черный ящик</b>!"
                    + "\nДостанеться он только ОДНОМУ!"
                    + "\nВнутри лежит артефакт, бонус или анти-бонус")),
        new SimpleStickerActionChecker(CHAT_ID, StickerType.GIFT));
    verify(userArtifactService).clearUserArtifacts(CHAT_ID);
  }

  @Test
  public void processTask_wednesday() {
    // given
    doReturn(LocalDate.of(2021, 9, 15)).when(dateHelper).now();
    doReturn(Collections.singletonList(CHAT_ID)).when(telegramService).getChatIds();

    // when
    task.processTask();

    // then
    assertEquals(DayOfWeek.WEDNESDAY, dateHelper.now().getDayOfWeek());
    checkActions(
        new TextBotActionChecker(
            CHAT_ID,
            new SimpleText(
                "Это <b>черный ящик</b>!"
                    + "\nДостанеться он только ОДНОМУ!"
                    + "\nВнутри лежит артефакт, бонус или анти-бонус")),
        new SimpleStickerActionChecker(CHAT_ID, StickerType.GIFT));
    verify(userArtifactService).clearUserArtifacts(CHAT_ID);
  }

  @Test
  public void processTask_saturday() {
    // given
    doReturn(LocalDate.of(2021, 9, 18)).when(dateHelper).now();
    doReturn(Collections.singletonList(CHAT_ID)).when(telegramService).getChatIds();

    // when
    task.processTask();

    // then
    assertEquals(DayOfWeek.SATURDAY, dateHelper.now().getDayOfWeek());
    checkActions(
        new TextBotActionChecker(
            CHAT_ID,
            new SimpleText(
                "Это <b>черный ящик</b>!"
                    + "\nДостанеться он только ОДНОМУ!"
                    + "\nВнутри лежит артефакт, бонус или анти-бонус")),
        new SimpleStickerActionChecker(CHAT_ID, StickerType.GIFT));
    verify(userArtifactService).clearUserArtifacts(CHAT_ID);
  }
}
