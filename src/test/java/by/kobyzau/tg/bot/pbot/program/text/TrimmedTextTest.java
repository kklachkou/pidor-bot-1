package by.kobyzau.tg.bot.pbot.program.text;

import org.junit.Assert;
import org.junit.Test;

public class TrimmedTextTest {

  @Test
  public void text_null() {
    // given
    Text text = new TrimmedText((Text) null);

    // then
    Assert.assertEquals("", text.text());
    Assert.assertEquals("", text.toString());
  }

  @Test
  public void text_blank() {
    // given
    String blankText = " ";
    Text text = new TrimmedText(blankText);

    // then
    Assert.assertEquals("", text.toString());
    Assert.assertEquals("", text.text());
  }

  @Test
  public void text_words() {
    // given
    String words = " some words ";
    Text text = new TrimmedText(words);

    // then
    Assert.assertEquals(words.trim(), text.toString());
    Assert.assertEquals(words.trim(), text.text());
  }
}
