package by.kobyzau.tg.bot.pbot.collectors;

import by.kobyzau.tg.bot.pbot.tg.action.BotAction;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("test")
public class CollectionBotActionCollector extends AbstractBotActionCollector {

  private List<BotAction<?>> actionList = new ArrayList<>();

  @Override
  public void add(BotAction<?> botAction) {
    actionList.add(botAction);
    //System.out.println(botAction);
  }

  public List<BotAction<?>> getActionList() {
    return actionList;
  }
}
