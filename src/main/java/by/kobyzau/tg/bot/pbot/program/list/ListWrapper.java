package by.kobyzau.tg.bot.pbot.program.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListWrapper<T> implements ListProvider<T> {

  private final List<ListProvider<T>> providers;

  public ListWrapper(List<ListProvider<T>> providers) {
    this.providers = providers;
  }

  public ListWrapper(ListProvider<T> provider) {
    this(Collections.singletonList(provider));
  }

  public ListWrapper(ListProvider<T> p1, ListProvider<T> p2) {
    this(Arrays.asList(p1, p2));
  }

  @Override
  public List<T> getList() {
    return providers.stream()
        .map(ListProvider::getList)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }
}
