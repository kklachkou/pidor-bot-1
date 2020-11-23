package by.kobyzau.tg.bot.pbot;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class RangeMatcher extends BaseMatcher<Integer> {
  private final int from;
  private final int to;

  public RangeMatcher(int from, int to) {
    this.from = from;
    this.to = to;
  }

  @Override
  public boolean matches(Object o) {
    if (o instanceof Integer) {
      int val = (Integer) o;
      return val >= from && val <= to;
    }
    return false;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(from + "").appendText(" - ").appendText(to + "");
  }
}
