package by.kobyzau.tg.bot.pbot.program.selection;

import java.util.function.Function;

public interface Selection<T> {

  T next();

  default <N> Selection<N> map(Function<T, N> mapper) {
    return new MappedSelection<>(mapper, this);
  }
}
