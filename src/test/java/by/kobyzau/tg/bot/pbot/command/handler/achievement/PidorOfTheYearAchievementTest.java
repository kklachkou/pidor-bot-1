package by.kobyzau.tg.bot.pbot.command.handler.achievement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import by.kobyzau.tg.bot.pbot.handlers.command.handler.achievement.Achievement;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.achievement.PidorOfTheYearAchievement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class PidorOfTheYearAchievementTest {

  private final int chatId = 123;
  @Mock private PidorService pidorService;
  @Mock private DailyPidorRepository dailyPidorRepository;

  private Achievement achievement;

  @Before
  public void init() {
    this.achievement = new PidorOfTheYearAchievement(pidorService, dailyPidorRepository);
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
  public void getAchievementInfo_twoYears() {
    // given
    int userCurrentYear = 1;
    int userPrevYear = 2;
    int currentYear = DateUtil.now().getYear() - 1;

    doReturn(
            Arrays.asList(
                getDailyPidor(currentYear, userCurrentYear),
                getDailyPidor(currentYear - 1, userPrevYear)))
        .when(dailyPidorRepository)
        .getByChat(chatId);
    doReturn(Optional.of(getPidor(userCurrentYear)))
        .when(pidorService)
        .getPidor(chatId, userCurrentYear);
    doReturn(Optional.of(getPidor(userPrevYear))).when(pidorService).getPidor(chatId, userPrevYear);

    // when
    Optional<Text> achievementInfo = achievement.getAchievementInfo(chatId);

    // then
    assertTrue(achievementInfo.isPresent());

    assertEquals(
        new ParametizedText(
                "Были пидором года:\n\n\uD83E\uDD47 {0} - Пидор {1} года"
                    + "\n\uD83E\uDD48 {2} - Пидор {3} года",
                new FullNamePidorText(getPidor(userCurrentYear)),
                new SimpleText(String.valueOf(currentYear)),
                new FullNamePidorText(getPidor(userPrevYear)),
                new SimpleText(String.valueOf(currentYear - 1)))
            .text(),
        achievementInfo.get().text());
  }

  @Test
  public void getAchievementInfo_skipCurrentYear() {
    // given
    int userCurrentYear = 1;
    int currentYear = DateUtil.now().getYear();

    doReturn(Collections.singletonList(getDailyPidor(currentYear, userCurrentYear)))
        .when(dailyPidorRepository)
        .getByChat(chatId);
    doReturn(Optional.of(getPidor(userCurrentYear)))
        .when(pidorService)
        .getPidor(chatId, userCurrentYear);

    // when
    Optional<Text> achievementInfo = achievement.getAchievementInfo(chatId);

    // then
    assertFalse(achievementInfo.isPresent());
  }

  private Pidor getPidor(int id) {
    return new Pidor(id, chatId, "FN:" + id);
  }

  private DailyPidor getDailyPidor(int year, int id) {
    DailyPidor dailyPidor = new DailyPidor();
    dailyPidor.setLocalDate(LocalDate.of(year, 2, 2));
    dailyPidor.setId(id);
    dailyPidor.setPlayerTgId(id);
    dailyPidor.setChatId(chatId);
    return dailyPidor;
  }
}
