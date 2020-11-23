package by.kobyzau.tg.bot.pbot.command.handler.achievement;

import by.kobyzau.tg.bot.pbot.handlers.command.handler.achievement.Achievement;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.achievement.TopNaturalAchievement;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class TopNaturalAchievementTest {

  private final int chatId = 123;
  @Mock private PidorService pidorService;
  @Mock private DailyPidorRepository dailyPidorRepository;

  private Achievement achievement;

  @Before
  public void init() {
    this.achievement = new TopNaturalAchievement(pidorService, dailyPidorRepository);
  }

  @Test
  public void getAchievementInfo_noDailyPidors() {
    // given
    doReturn(Collections.emptyList()).when(dailyPidorRepository).getByChat(chatId);

    // when
    Optional<Text> achievementInfo = achievement.getAchievementInfo(chatId);

    // then
    assertFalse(achievementInfo.isPresent());
  }

  @Test
  public void topPidor1() {
    // given
    Mockito.doReturn(
            Arrays.asList(
                getDailyPidor(1),
                getDailyPidor(2),
                getDailyPidor(1),
                getDailyPidor(1),
                getDailyPidor(2),
                getDailyPidor(2),
                getDailyPidor(2),
                getDailyPidor(2),
                getDailyPidor(1)))
        .when(dailyPidorRepository)
        .getByChat(chatId);
    doReturn(Optional.of(getPidor(1))).when(pidorService).getPidor(chatId, 1);
    doReturn(Optional.of(getPidor(2))).when(pidorService).getPidor(chatId, 2);

    // when
    Optional<Text> achievementInfo = achievement.getAchievementInfo(chatId);

    // then
    assertTrue(achievementInfo.isPresent());
    assertEquals(
        new TextBuilder(
                new SimpleText("Меньше всего был пидором дня:"),
                new NewLineText(),
                new NewLineText(),
                new ParametizedText(
                    "{0} - был пидором всего {1} раз(а) (44%)",
                    new FullNamePidorText(getPidor(1)), new SimpleText(String.valueOf(4))))
                .append(new NewLineText())
            .text(),
        achievementInfo.get().text());
  }

  @Test
  public void topPidor2() {
    // given
    Mockito.doReturn(
            Arrays.asList(
                getDailyPidor(1),
                getDailyPidor(2),
                getDailyPidor(1),
                getDailyPidor(2),
                getDailyPidor(1),
                getDailyPidor(2),
                getDailyPidor(1)))
        .when(dailyPidorRepository)
        .getByChat(chatId);
    doReturn(Optional.of(getPidor(1))).when(pidorService).getPidor(chatId, 1);
    doReturn(Optional.of(getPidor(2))).when(pidorService).getPidor(chatId, 2);

    // when
    Optional<Text> achievementInfo = achievement.getAchievementInfo(chatId);

    // then
    assertTrue(achievementInfo.isPresent());
    assertEquals(
        new TextBuilder(
                new SimpleText("Меньше всего был пидором дня:"),
                new NewLineText(),
                new NewLineText(),
                new ParametizedText(
                    "{0} - был пидором всего {1} раз(а) (42%)",
                    new FullNamePidorText(getPidor(2)), new SimpleText(String.valueOf(3))))
                .append(new NewLineText())
            .text(),
        achievementInfo.get().text());
  }

  private Pidor getPidor(int id) {
    return new Pidor(id, chatId, "FN:" + id);
  }

  private DailyPidor getDailyPidor(int id) {
    DailyPidor dailyPidor = new DailyPidor();
    dailyPidor.setPlayerTgId(id);
    dailyPidor.setChatId(chatId);
    return dailyPidor;
  }
}
