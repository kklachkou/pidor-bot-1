package by.kobyzau.tg.bot.pbot.program.shuffler;

import java.util.List;
import java.util.function.Function;

public interface Shuffler<T> extends Function<List<T>, List<T>> {}
