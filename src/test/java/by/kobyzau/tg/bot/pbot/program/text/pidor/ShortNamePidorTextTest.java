package by.kobyzau.tg.bot.pbot.program.text.pidor;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorMark;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeSet;

import static by.kobyzau.tg.bot.pbot.model.PidorMark.LAST_PIDOR_OF_DAY;
import static by.kobyzau.tg.bot.pbot.model.PidorMark.PIDOR_OF_YEAR;
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
    Text text =
        new ShortNamePidorText(
            new Pidor(1, 1, "FullName", Collections.singletonList(PIDOR_OF_YEAR)));

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
    pidor.setPidorMarks(Collections.singletonList(PIDOR_OF_YEAR));

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
    pidor.setPidorMarks(Collections.singletonList(PIDOR_OF_YEAR));

    Text text = new ShortNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    Assert.assertEquals("Bob Jones \uD83D\uDC51", result);
  }

  /**/
  @Test
  public void textToString_pidor() {
    // given
    Text text =
        new ShortNamePidorText(
            new Pidor(1, 1, "FullName", Collections.singletonList(LAST_PIDOR_OF_DAY)));

    // then
    assertEquals(text.text(), text.toString());
  }

  @Test
  public void text_withNickname_pidor() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("username");
    pidor.setNickname("Nickname");
    pidor.setPidorMarks(Collections.singletonList(LAST_PIDOR_OF_DAY));

    Text text = new ShortNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    Assert.assertEquals("Bob Jones (Nickname) \uD83D\uDC13", result);
  }

  @Test
  public void text_withoutNickname_pidor() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("username");
    pidor.setPidorMarks(Collections.singletonList(LAST_PIDOR_OF_DAY));

    Text text = new ShortNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    Assert.assertEquals("Bob Jones \uD83D\uDC13", result);
  }
  /**/
  @Test
  public void textToString_pidor_crown() {
    // given
    Text text =
        new ShortNamePidorText(
            new Pidor(1, 1, "FullName", Arrays.asList(PIDOR_OF_YEAR, LAST_PIDOR_OF_DAY)));

    // then
    assertEquals(text.text(), text.toString());
  }

  @Test
  public void text_withNickname_pidor_crown() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("username");
    pidor.setNickname("Nickname");
    pidor.setPidorMarks(Arrays.asList(PIDOR_OF_YEAR, LAST_PIDOR_OF_DAY));

    Text text = new ShortNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    Assert.assertEquals("Bob Jones (Nickname) \uD83D\uDC51 \uD83D\uDC13", result);
  }

  @Test
  public void text_withoutNickname_pidor_crown() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("username");
    pidor.setPidorMarks(Arrays.asList(PIDOR_OF_YEAR, LAST_PIDOR_OF_DAY));

    Text text = new ShortNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    Assert.assertEquals("Bob Jones \uD83D\uDC51 \uD83D\uDC13", result);
  }

  @Test
  public void text_withUserNameWithNickname_allMarks() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("Username");
    pidor.setNickname("Nickname");
    pidor.setPidorMarks(Arrays.asList(PidorMark.values()));
    Text text = new ShortNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("Bob Jones (Nickname) \uD83D\uDC51 \uD83D\uDC13 \uD83E\uDDA0", result);
  }

  @Test
  public void text_withUserNameWithNickname_allArtifacts() {
    // given
    Pidor pidor = new Pidor();
    pidor.setFullName("Bob Jones");
    pidor.setUsername("Username");
    pidor.setNickname("Nickname");
    pidor.setArtifacts(new TreeSet<>(Arrays.asList(ArtifactType.values())));
    Text text = new ShortNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals("Bob Jones (Nickname) \uD83E\uDDF2 \uD83E\uDDD9\uD83C\uDFFB\u200D♂", result);
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
    Text text = new ShortNamePidorText(pidor);

    // when
    String result = text.text();

    // then
    assertEquals(
        "Bob Jones (Nickname) \uD83E\uDDF2 \uD83E\uDDD9\uD83C\uDFFB\u200D♂ \uD83D\uDC51 \uD83D\uDC13 \uD83E\uDDA0",
        result);
  }
}
