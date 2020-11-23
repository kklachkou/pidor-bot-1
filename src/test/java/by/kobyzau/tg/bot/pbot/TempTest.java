package by.kobyzau.tg.bot.pbot;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TempTest {

  @Test
  public void instant() {
    Map<Integer, Integer> numWinsById = new HashMap<>();
    numWinsById.put(10, 2);
    numWinsById.put(5, 3);
    int maxWinNum = numWinsById.values().stream().max(Integer::compareTo).orElse(0);
    Assert.assertEquals(3, maxWinNum);
  }
}
