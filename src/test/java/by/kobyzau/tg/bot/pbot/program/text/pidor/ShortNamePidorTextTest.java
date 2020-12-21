package by.kobyzau.tg.bot.pbot.program.text.pidor;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShortNamePidorTextTest {

  @Test
  public void textToString() {
    // given
    Text text = new ShortNamePidorText(new Pidor(1, 1, "FullName"));

    // then
    assertEquals(text.text(), text.toString());
  }

  @Test
  public void text_withNickname() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("username");
    pidor.setNickname("Nickname");

    Text text = new ShortNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    Assert.assertEquals("Bob Jones (Nickname)", result);
  }

  @Test
  public void text_withoutNickname() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("username");

    Text text = new ShortNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    Assert.assertEquals("Bob Jones", result);
  }



  @Test
  public void textToString_crown() {
    // given
    Text text = new ShortNamePidorText(new Pidor(1, 1, "FullName", true));

    // then
    assertEquals(text.text(), text.toString());
  }

  @Test
  public void text_withNickname_crown() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("username");
    pidor.setNickname("Nickname");
    pidor.setPidorOfYear(true);

    Text text = new ShortNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    Assert.assertEquals("Bob Jones (Nickname) \uD83D\uDC51", result);
  }

  @Test
  public void text_withoutNickname_crown() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("username");
    pidor.setPidorOfYear(true);

    Text text = new ShortNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    Assert.assertEquals("Bob Jones \uD83D\uDC51", result);
  }
}
