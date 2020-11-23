package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.dice.DiceRepository;
import by.kobyzau.tg.bot.pbot.service.impl.DiceServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(MockitoJUnitRunner.class)
public class DiceServiceTest {

  private final long chatId = 1;
  @Mock private DiceRepository diceRepository;
  @Mock private List<EmojiGame> games;

  @Mock private PidorService pidorService;
  @Mock private CalendarSchedule calendarSchedule;
  @InjectMocks private DiceService service = new DiceServiceImpl();

  @Test
  public void getNumPidorsToPlay_firstNumbers() {
    Map<Integer, Integer> countToResult = new HashMap<>();
    countToResult.put(0, 0);
    countToResult.put(1, 1);
    countToResult.put(2, 2);
    countToResult.put(3, 3);
    countToResult.put(4, 4);
    countToResult.put(5, 5);
    countToResult.put(6, 4);
    countToResult.put(7, 5);
    countToResult.put(8, 6);
    countToResult.put(9, 7);
    countToResult.put(10, 8);
    countToResult.put(100, 80);

    for (Map.Entry<Integer, Integer> data : countToResult.entrySet()) {
      Mockito.doReturn(
              IntStream.range(0, data.getKey())
                  .mapToObj(i -> new Pidor())
                  .collect(Collectors.toList()))
          .when(pidorService)
          .getByChat(chatId);
      Integer numPidorsToPlay = service.getNumPidorsToPlay(chatId);
      Assert.assertEquals(data.getValue(), numPidorsToPlay);
    }
  }
}
