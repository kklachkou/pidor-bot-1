package by.kobyzau.tg.bot.pbot.program.text.pidor;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorMark;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;

import static by.kobyzau.tg.bot.pbot.model.PidorMark.*;
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

  @Test
  public void textToString_crown() {
    // given
    Text text = new FullNamePidorText(new Pidor(1, 1, "FullName", Collections.singletonList(PIDOR_OF_YEAR)));

    // then
    assertEquals(text.text(), text.toString());
  }

  @Test
  public void text_withUserNameWithNickname_crown() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("Username");
    pidor.setNickname("Nickname");
    pidor.setPidorMarks(Collections.singletonList(PIDOR_OF_YEAR));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("@Username (Nickname) \uD83D\uDC51", result);
  }

  @Test
  public void text_withUserNameWithoutNickname_crown() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("Username");
    pidor.setPidorMarks(Collections.singletonList(PIDOR_OF_YEAR));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("@Username (Bob Jones) \uD83D\uDC51", result);
  }

  @Test
  public void text_withoutUserNameWithNickname_crown() {
    // given
    Pidor pidor = new Pidor();
    pidor.setTgId(25);
    pidor.setFullName("Bob Jones");
    pidor.setNickname("Nickname");
    pidor.setPidorMarks(Collections.singletonList(PIDOR_OF_YEAR));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("<a href=\"tg://user?id=25\">Bob Jones</a> (Nickname) \uD83D\uDC51", result);
  }

  @Test
  public void text_withoutUserNameWithoutNickname_crown() {
    // given
    Pidor pidor = new Pidor();
    pidor.setTgId(25);
    pidor.setFullName("Bob Jones");
    pidor.setPidorMarks(Collections.singletonList(PIDOR_OF_YEAR));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("<a href=\"tg://user?id=25\">Bob Jones</a> \uD83D\uDC51", result);
  }

  /********/

  @Test
  public void textToString_pidor() {
    // given
    Text text = new FullNamePidorText(new Pidor(1, 1, "FullName", Collections.singletonList(LAST_PIDOR_OF_DAY)));

    // then
    assertEquals(text.text(), text.toString());
  }

  @Test
  public void text_withUserNameWithNickname_pidor() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("Username");
    pidor.setNickname("Nickname");
    pidor.setPidorMarks(Collections.singletonList(LAST_PIDOR_OF_DAY));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("@Username (Nickname) \uD83D\uDC13", result);
  }

  @Test
  public void text_withUserNameWithoutNickname_pidor() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("Username");
    pidor.setPidorMarks(Collections.singletonList(LAST_PIDOR_OF_DAY));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("@Username (Bob Jones) \uD83D\uDC13", result);
  }

  @Test
  public void text_withoutUserNameWithNickname_pidor() {
    // given
    Pidor pidor = new Pidor();
    pidor.setTgId(25);
    pidor.setFullName("Bob Jones");
    pidor.setNickname("Nickname");
    pidor.setPidorMarks(Collections.singletonList(LAST_PIDOR_OF_DAY));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("<a href=\"tg://user?id=25\">Bob Jones</a> (Nickname) \uD83D\uDC13", result);
  }

  @Test
  public void text_withoutUserNameWithoutNickname_pidor() {
    // given
    Pidor pidor = new Pidor();
    pidor.setTgId(25);
    pidor.setFullName("Bob Jones");
    pidor.setPidorMarks(Collections.singletonList(LAST_PIDOR_OF_DAY));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("<a href=\"tg://user?id=25\">Bob Jones</a> \uD83D\uDC13", result);
  }

  /********/

  @Test
  public void textToString_pidor_crown() {
    // given
    Text text = new FullNamePidorText(new Pidor(1, 1, "FullName", Arrays.asList(PIDOR_OF_YEAR, LAST_PIDOR_OF_DAY)));

    // then
    assertEquals(text.text(), text.toString());
  }


  @Test
  public void text_withUserNameWithNickname_allMarks() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("Username");
    pidor.setNickname("Nickname");
    pidor.setPidorMarks(Arrays.asList(PidorMark.values()));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("@Username (Nickname) \uD83D\uDC51 \uD83D\uDC13 \uD83E\uDDA0", result);
  }


  @Test
  public void text_withUserNameWithNickname_allArtifacts() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("Username");
    pidor.setNickname("Nickname");
    pidor.setArtifacts(new TreeSet<>(Arrays.asList(ArtifactType.values())));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("@Username (Nickname) \uD83E\uDDF2 \uD83E\uDDD9\uD83C\uDFFB\u200D♂", result);
  }


  @Test
  public void text_withUserNameWithNickname_allMarks_allArtifacts() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("Username");
    pidor.setNickname("Nickname");
    pidor.setPidorMarks(Arrays.asList(PidorMark.values()));
    pidor.setArtifacts(new TreeSet<>(Arrays.asList(ArtifactType.values())));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals(
        "@Username (Nickname) \uD83E\uDDF2 \uD83E\uDDD9\uD83C\uDFFB\u200D♂ \uD83D\uDC51 \uD83D\uDC13 \uD83E\uDDA0",
        result);
  }

  @Test
  public void text_withUserNameWithNickname_pidor_crown() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("Username");
    pidor.setNickname("Nickname");
    pidor.setPidorMarks(Arrays.asList(PIDOR_OF_YEAR, LAST_PIDOR_OF_DAY));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("@Username (Nickname) \uD83D\uDC51 \uD83D\uDC13", result);
  }

  @Test
  public void text_withUserNameWithoutNickname_pidor_crown() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("Username");
    pidor.setPidorMarks(Arrays.asList(PIDOR_OF_YEAR, LAST_PIDOR_OF_DAY));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("@Username (Bob Jones) \uD83D\uDC51 \uD83D\uDC13", result);
  }

  @Test
  public void text_withoutUserNameWithNickname_pidor_crown() {
    // given
    Pidor pidor = new Pidor();
    pidor.setTgId(25);
    pidor.setFullName("Bob Jones");
    pidor.setNickname("Nickname");
    pidor.setPidorMarks(Arrays.asList(PIDOR_OF_YEAR, LAST_PIDOR_OF_DAY));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals(
        "<a href=\"tg://user?id=25\">Bob Jones</a> (Nickname) \uD83D\uDC51 \uD83D\uDC13", result);
  }

  @Test
  public void text_withoutUserNameWithoutNickname_pidor_crown() {
    // given
    Pidor pidor = new Pidor();
    pidor.setTgId(25);
    pidor.setFullName("Bob Jones");
    pidor.setPidorMarks(Arrays.asList(PIDOR_OF_YEAR, LAST_PIDOR_OF_DAY));
    Text text = new FullNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("<a href=\"tg://user?id=25\">Bob Jones</a> \uD83D\uDC51 \uD83D\uDC13", result);
  }
}
