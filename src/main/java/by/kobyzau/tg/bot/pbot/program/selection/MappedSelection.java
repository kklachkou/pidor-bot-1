package by.kobyzau.tg.bot.pbot.program.selection;

import java.util.function.Function;

public class MappedSelection<O, N> implements Selection<N> {
  private final Function<O, N> mapper;
  private final Selection<O> oldSelection;

  public MappedSelection(Function<O, N> mapper, Selection<O> oldSelection) {
    this.mapper = mapper;
    this.oldSelection = oldSelection;
  }

  @Override
  public N next() {
    return mapper.apply(oldSelection.next());
  }
}
