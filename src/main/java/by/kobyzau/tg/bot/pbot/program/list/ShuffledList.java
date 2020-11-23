package by.kobyzau.tg.bot.pbot.program.list;

import by.kobyzau.tg.bot.pbot.util.CollectionUtil;

import java.util.List;

public class ShuffledList<T> implements ListProvider<T> {
  private final ListProvider<T> listProvider;

  public ShuffledList(ListProvider<T> listProvider) {
    this.listProvider = listProvider;
  }

  @Override
  public List<T> getList() {
    return CollectionUtil.getRandomList(listProvider.getList());
  }
}
