package by.kobyzau.tg.bot.pbot.program.text.pidor;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FullNamePidorTextTest {

  @Test
  public void textToString() {
    // given
    Text text = new FullNamePidorText(new Pidor(1, 1, "FullName"));

    // then
    assertEquals(text.text(), text.toString());
  }

  @Test
  public void text_withUserNameWithNickname() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("Username");
    pidor.setNickname("Nickname");
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("@Username (Nickname)", result);
  }

  @Test
  public void text_withUserNameWithoutNickname() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("Username");
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("@Username (Bob Jones)", result);
  }

  @Test
  public void text_withoutUserNameWithNickname() {
    // given
    Pidor pidor = new Pidor();
    pidor.setTgId(25);
    pidor.setFullName("Bob Jones");
    pidor.setNickname("Nickname");
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("<a href=\"tg://user?id=25\">Bob Jones</a> (Nickname)", result);
  }

  @Test
  public void text_withoutUserNameWithoutNickname() {
    // given
    Pidor pidor = new Pidor();
    pidor.setTgId(25);
    pidor.setFullName("Bob Jones");
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("<a href=\"tg://user?id=25\">Bob Jones</a>", result);
  }
}
