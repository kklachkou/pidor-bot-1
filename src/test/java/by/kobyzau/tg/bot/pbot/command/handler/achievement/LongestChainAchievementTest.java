package by.kobyzau.tg.bot.pbot.command.handler.achievement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import by.kobyzau.tg.bot.pbot.handlers.command.handler.achievement.Achievement;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.achievement.LongestChainAchievement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;

@RunWith(Parameterized.class)
public class LongestChainAchievementTest {

  private static final long CHAT_ID = 123;

  @Parameterized.Parameter public List<DailyPidor> dailyPidors;

  @Parameterized.Parameter(1)
  public Text expected;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {

    int day = 1;
    List<Object[]> params = new ArrayList<>();
    params.add(new Object[] {Collections.emptyList(), null});
    params.add(
        new Object[] {
          Collections.singletonList(getDailyPidor(2, day++)),
          new TextBuilder(
              new SimpleText("Был пидором наибольшее число дней подряд:"),
              new NewLineText(),
              new NewLineText(),
              new ParametizedText(
                  "{0} - {1} дней подряд", new FullNamePidorText(getPidor(2)), new SimpleText("1")))
        });
    day = 1;
    params.add(
        new Object[] {
          Arrays.asList(getDailyPidor(2, 1), getDailyPidor(2, day++)),
          new TextBuilder(
              new SimpleText("Был пидором наибольшее число дней подряд:"),
              new NewLineText(),
              new NewLineText(),
              new ParametizedText(
                  "{0} - {1} дней подряд", new FullNamePidorText(getPidor(2)), new SimpleText("2")))
        });
    params.add(
        new Object[] {
          Arrays.asList(
              getDailyPidor(2, 1), getDailyPidor(1, 2), getDailyPidor(1, 4), getDailyPidor(2, 5)),
          new TextBuilder(
              new SimpleText("Был пидором наибольшее число дней подряд:"),
              new NewLineText(),
              new NewLineText(),
              new ParametizedText(
                  "{0} - {1} дней подряд", new FullNamePidorText(getPidor(1)), new SimpleText("2")))
        });


    day = 1;
    params.add(
        new Object[] {
          Arrays.asList(
              getDailyPidor(2, day++),
              getDailyPidor(1, day++),
              getDailyPidor(1, day++),
              getDailyPidor(2, day++),
              getDailyPidor(2, day++),
              getDailyPidor(2, day++)),
          new TextBuilder(
              new SimpleText("Был пидором наибольшее число дней подряд:"),
              new NewLineText(),
              new NewLineText(),
              new ParametizedText(
                  "{0} - {1} дней подряд", new FullNamePidorText(getPidor(2)), new SimpleText("3")))
        });

    day = 1;
    params.add(
        new Object[] {
          Arrays.asList(
              getDailyPidor(2, day++),
              getDailyPidor(1, day++),
              getDailyPidor(1, day++),
              getDailyPidor(2, day++),
              getDailyPidor(2, day++),
              getDailyPidor(2, day++),
              getDailyPidor(1, day++)),
          new TextBuilder(
              new SimpleText("Был пидором наибольшее число дней подряд:"),
              new NewLineText(),
              new NewLineText(),
              new ParametizedText(
                  "{0} - {1} дней подряд", new FullNamePidorText(getPidor(2)), new SimpleText("3")))
        });

    day =1;
    params.add(
        new Object[] {
          Arrays.asList(
              getDailyPidor(3, day++),
              getDailyPidor(3, day++),
              getDailyPidor(3, day++),
              getDailyPidor(3, day++),
              getDailyPidor(2, day++),
              getDailyPidor(1, day++),
              getDailyPidor(1, day++),
              getDailyPidor(2, day++),
              getDailyPidor(2, day++),
              getDailyPidor(2, day++),
              getDailyPidor(1, day++)),
          new TextBuilder(
              new SimpleText("Был пидором наибольшее число дней подряд:"),
              new NewLineText(),
              new NewLineText(),
              new ParametizedText(
                  "{0} - {1} дней подряд", new FullNamePidorText(getPidor(3)), new SimpleText("4")))
        });

    return params;
  }

  @Test
  public void getAchievementInfo() {
    // given
    PidorService pidorService = mock(PidorService.class);
    DailyPidorRepository dailyPidorRepository = mock(DailyPidorRepository.class);
    doReturn(dailyPidors).when(dailyPidorRepository).getByChat(CHAT_ID);
    doAnswer(
            invocationOnMock -> {
              long id = (Long) invocationOnMock.getArguments()[1];
              return Optional.of(getPidor(id));
            })
        .when(pidorService)
        .getPidor(anyLong(), anyLong());
    Achievement achievement = new LongestChainAchievement(pidorService, dailyPidorRepository);

    // when
    Optional<Text> achievementInfo = achievement.getAchievementInfo(CHAT_ID);

    if (expected == null) {
      assertFalse(achievementInfo.isPresent());
    } else {
      assertTrue(achievementInfo.isPresent());
      assertEquals(expected.text(), achievementInfo.get().text());
    }
  }

  private static Pidor getPidor(long id) {
    Pidor pidor = new Pidor();
    pidor.setId(id);
    pidor.setTgId(id);
    pidor.setFullName("FN:" + id);
    return pidor;
  }

  private static DailyPidor getDailyPidor(long id, int day) {
    DailyPidor dailyPidor = new DailyPidor();
    dailyPidor.setChatId(CHAT_ID);
    dailyPidor.setPlayerTgId(id);
    dailyPidor.setId(id);
    dailyPidor.setLocalDate(LocalDate.of(2020, 1, day));
    return dailyPidor;
  }
}
