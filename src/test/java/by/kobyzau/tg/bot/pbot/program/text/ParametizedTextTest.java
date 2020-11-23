package by.kobyzau.tg.bot.pbot.program.text;

import org.junit.Assert;
import org.junit.Test;

public class ParametizedTextTest {

  @Test
  public void text_noParams() {
    // given
    SimpleText template = new SimpleText("some {0} text");
    Text text = new ParametizedText(template);

    // then
    Assert.assertEquals(template.text(), text.text());
    Assert.assertEquals(template.text(), text.toString());
  }

  @Test
  public void text_hasParams() {
    // given
    SimpleText template = new SimpleText("First param: {1} and second: {0} params");
    Text text = new ParametizedText(template, new SimpleText("First"), new SimpleText("Second"));

    // then
    Assert.assertEquals("First param: Second and second: First params", text.toString());
    Assert.assertEquals("First param: Second and second: First params", text.text());
  }
}
