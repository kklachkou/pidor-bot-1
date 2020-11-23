package by.kobyzau.tg.bot.pbot.program.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TextBuilder implements Text {

  private final List<Text> textList;

  public TextBuilder() {
    this.textList = new ArrayList<>();
  }

  public TextBuilder(Text... text) {
    this.textList = new ArrayList<>();
    Arrays.asList(text).forEach(this::append);
  }

  public TextBuilder append(Text text) {
    this.textList.add(text);
    return this;
  }

  @Override
  public String text() {
    return textList.stream().filter(Objects::nonNull).map(Text::text).collect(Collectors.joining());
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
