package by.kobyzau.tg.bot.pbot.util.helper;

import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CollectionHelper {

  public <T> T getRandomValue(Collection<T> c) {
    return CollectionUtil.getRandomValue(c);
  }
}
