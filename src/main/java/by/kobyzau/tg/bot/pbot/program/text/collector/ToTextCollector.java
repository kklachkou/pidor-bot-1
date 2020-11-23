package by.kobyzau.tg.bot.pbot.program.text.collector;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import by.kobyzau.tg.bot.pbot.program.text.EmptyText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;

public class ToTextCollector implements Collector<Text, TextBuilder, Text> {

  private final Text delimiter;

  public ToTextCollector() {
    this(new EmptyText());
  }

  public ToTextCollector(Text delimiter) {
    this.delimiter = delimiter;
  }

  @Override
  public Supplier<TextBuilder> supplier() {
    return TextBuilder::new;
  }

  @Override
  public BiConsumer<TextBuilder, Text> accumulator() {
    return (textBuilder, text) -> {
      if (!textBuilder.isEmpty()) {
        textBuilder.append(delimiter);
      }
      textBuilder.append(text);
    };
  }

  @Override
  public BinaryOperator<TextBuilder> combiner() {
    return (t1, t2) -> new TextBuilder(t1).append(delimiter).append(t2);
  }

  @Override
  public Function<TextBuilder, Text> finisher() {
    return textBuilder -> textBuilder;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return Collections.singleton(Characteristics.IDENTITY_FINISH);
  }
}
