package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorDice;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.dice.DiceRepository;
import by.kobyzau.tg.bot.pbot.service.impl.DiceServiceImpl;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(Parameterized.class)
public class DiceServiceNeedFinalizeTest {

  @Mock PidorService pidorService;
  @Mock DiceRepository diceRepository;
  @Mock DailyPidorRepository dailyPidorRepository;

  @InjectMocks private DiceServiceImpl service = new DiceServiceImpl();

  @Parameterized.Parameter(0)
  public int numPidors;

  @Parameterized.Parameter(1)
  public int numDices;

  @Parameterized.Parameter(2)
  public boolean expected;

  private final long chatId = 132;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(
            new Object[] {1, 1, true},
            new Object[] {1, 2, true},
            new Object[] {1, 3, true},
            new Object[] {1, 4, true},
            new Object[] {1, 5, true},

            new Object[] {3, 1, false},
            new Object[] {3, 2, false},
            new Object[] {3, 3, true},
            new Object[] {3, 4, true},
            new Object[] {3, 5, true},

            new Object[] {5, 1, false},
            new Object[] {5, 2, false},
            new Object[] {5, 3, false},
            new Object[] {5, 4, false},
            new Object[] {5, 5, true},
            new Object[] {5, 6, true},
            new Object[] {5, 7, true},

            new Object[] {10, 1, false},
            new Object[] {10, 2, false},
            new Object[] {10, 3, false},
            new Object[] {10, 4, false},
            new Object[] {10, 5, false},
            new Object[] {10, 6, false},
            new Object[] {10, 7, false},
            new Object[] {10, 8, true},
            new Object[] {10, 9, true},
            new Object[] {10, 10, true},
            new Object[] {10, 11, true},
            new Object[] {10, 12, true},
            new Object[] {10, 13, true}
        );
  }

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void needToFinalize() {
    // given
    doReturn(getPidors()).when(pidorService).getByChat(chatId);
    doReturn(getDices()).when(diceRepository).getAll();
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());

    // when
    boolean needToFinalize = service.needToFinalize(chatId);

    // then
    String desc = "Num: " + numPidors + ", dices: " + numDices;
    assertEquals(desc, expected, needToFinalize);
  }

  private List<Pidor> getPidors() {
    List<Pidor> pidors = new ArrayList<>();
    for (int i = 0; i < numPidors; i++) {
      pidors.add(new Pidor(i, chatId, "user " + i));
    }
    return pidors;
  }

  private List<PidorDice> getDices() {
    List<PidorDice> dices = new ArrayList<>();
    for (int i = 0; i < numDices; i++) {
      dices.add(new PidorDice(i, chatId, DateUtil.now(), 3));
    }
    return dices;
  }
}
