package by.kobyzau.tg.bot.pbot.program.text;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;

public class ParametizedText implements Text {

  private final Text text;
  private final List<Text> params;

  public ParametizedText(Text text, @NotNull List<Text> params) {
    this.text = text;
    this.params = params;
  }

  public ParametizedText(Text text, Text... params) {
    this(text, Arrays.asList(params));
  }

  public ParametizedText(String text, Text... params) {
    this(new SimpleText(text), Arrays.asList(params));
  }

  @Override
  public String text() {
    String template = new NotBlankText(text).text();
    for (int i = 0; i < params.size(); i++) {
      template = template.replaceFirst("\\{" + i + "}", params.get(i).text());
    }
    return template;
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
