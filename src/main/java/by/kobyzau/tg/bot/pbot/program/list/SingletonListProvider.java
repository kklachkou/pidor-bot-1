package by.kobyzau.tg.bot.pbot.program.list;

import java.util.Collections;
import java.util.List;

public class SingletonListProvider<T> implements ListProvider<T> {

  private final T obj;

  public SingletonListProvider(T obj) {
    this.obj = obj;
  }

  @Override
  public List<T> getList() {
    return Collections.singletonList(obj);
  }
}
