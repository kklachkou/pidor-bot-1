package by.kobyzau.tg.bot.pbot.program.text;

import by.kobyzau.tg.bot.pbot.util.CollectionUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RandomText implements Text {

  private final Text text;

    public RandomText(String... text) {
        this.text = new SimpleText(CollectionUtil.getRandomValue(Arrays.asList(text)));
    }

    public RandomText(List<String> text) {
        this.text = new SimpleText(CollectionUtil.getRandomValue(text));
    }

  @Override
  public String text() {
    return text.text();
  }

  @Override
  public String toString() {
    return text();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof Text) {
      return ((Text) o).text().equals(text());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(text());
  }
}
