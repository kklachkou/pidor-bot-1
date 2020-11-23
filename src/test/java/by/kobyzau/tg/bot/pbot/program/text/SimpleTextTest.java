package by.kobyzau.tg.bot.pbot.program.text;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTextTest {

  @Test
  public void text_null() {
    // given
    Text text = new SimpleText(null);

    // then
    Assert.assertNull(text.toString());
    Assert.assertNull(text.text());
  }

  @Test
  public void text_blank() {
    // given
    String blankText = " ";
    Text text = new SimpleText(blankText);

    // then
    Assert.assertEquals(blankText, text.toString());
    Assert.assertEquals(blankText, text.text());
  }

  @Test
  public void text_words() {
    // given
    String words = "some words";
    Text text = new SimpleText(words);

    // then
    Assert.assertEquals(words, text.toString());
    Assert.assertEquals(words, text.text());
  }
}
