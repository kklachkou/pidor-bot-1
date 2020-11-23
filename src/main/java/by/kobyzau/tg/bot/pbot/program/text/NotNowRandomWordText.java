package by.kobyzau.tg.bot.pbot.program.text;

import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;

import java.util.Objects;

public class NotNowRandomWordText implements Text {

  private static final Selection<String> WORDS =
      new ConsistentSelection<>("Не сегодня", "Ещё рано", "Один момент", "Подожди");

  private final String word;

  public NotNowRandomWordText() {
    this.word = WORDS.next();
  }

  @Override
  public String text() {
    return word;
  }

  @Override
  public String toString() {
    return text();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof Text) {
      return text().equals(((Text) o).text());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text());
  }
}
