package by.kobyzau.tg.bot.pbot.program.text;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NotBlankTextTest {

  @Test
  public void text_nullText() {
    // given
    Text text = new NotBlankText((Text) null);

    // then
    assertEquals("", text.text());
    assertEquals("", text.toString());
  }

  @Test
  public void text_blankText() {
    // given
    Text blankText = new SimpleText(" ");
    Text text = new NotBlankText(blankText);

    // then
    assertEquals("", text.text());
    assertEquals("", text.toString());
  }

  @Test
  public void text_wordsText() {
    // given
    Text words = new SimpleText("some words");
    Text text = new NotBlankText(words);

    // then
    assertEquals(words.text(), text.toString());
    assertEquals(words.text(), text.text());
  }

  @Test
  public void text_nullString() {
    // given
    Text text = new NotBlankText((String) null);

    // then
    assertEquals("", text.toString());
    assertEquals("", text.text());
  }

  @Test
  public void text_blankString() {
    // given
    String blankText = " ";
    Text text = new NotBlankText(blankText);

    // then
    assertEquals("", text.text());
    assertEquals("", text.toString());
  }

  @Test
  public void text_wordsString() {
    // given
    String words = "some words";
    Text text = new NotBlankText(words);

    // then
    assertEquals(words, text.toString());
    assertEquals(words, text.text());
  }

  @Test
  public void text_null_ifBlank() {
    // given
    Text words = null;
    Text ifBlank = new SimpleText("ifBlank");
    Text text = new NotBlankText(words, ifBlank);

    // then
    assertEquals(ifBlank.text(), text.toString());
    assertEquals(ifBlank.text(), text.text());
  }

  @Test
  public void text_words_ifBlank() {
    // given
    Text words = new SimpleText("some text");
    Text ifBlank = new SimpleText("ifBlank");
    Text text = new NotBlankText(words, ifBlank);

    // then
    assertEquals(words.text(), text.toString());
    assertEquals(words.text(), text.text());
  }
}
