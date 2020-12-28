package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.collectors.CollectionBotActionCollector;
import org.junit.Assert;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class BotActionAbstractTest {

  @Spy private CollectionBotActionCollector botActionCollector = new CollectionBotActionCollector();

  protected void checkActions(List<BotActionChecker> checkerList) {
    for (int i = 0; i < checkerList.size(); i++) {
      if (i >= botActionCollector.getActionList().size()) {
        Assert.fail("No " + (i + 1) + " bot action");
      }
      checkerList.get(i).check(botActionCollector.getActionList().get(i));
    }
    assertEquals(
        "Invalid bot action count", checkerList.size(), botActionCollector.getActionList().size());
  }

  protected void checkActions(BotActionChecker... checkerList) {
    this.checkActions(Arrays.asList(checkerList));
  }

  protected void checkNoAnyActions() {
    this.checkActions(Collections.emptyList());
  }
}
