package by.kobyzau.tg.bot.pbot.telegraph;

import java.util.Optional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TelegraphTypeTest {

  private static final TelegraphType TYPE = TelegraphType.TEMP;

  @Test
  public void getLinkedId_withoutParam() {
    // when
    String linkedId = TYPE.getLinkedId();

    // then
    assertEquals(TYPE.name(), linkedId);
  }

  @Test
  public void getLinkedId_withSingleParam() {
    // given
    String param = "p1";

    // when
    String linkedId = TYPE.getLinkedId(param);

    // then
    assertEquals(TYPE.name() + ":" + param, linkedId);
  }

  @Test
  public void getLinkedId_withMultiParam() {
    // given
    String param1 = "p1";
    int param2 = 234;

    // when
    String linkedId = TYPE.getLinkedId(param1, param2);

    // then
    assertEquals(TYPE.name() + ":" + param1 + ":" + param2, linkedId);
  }

  @Test
  public void parseByLinkedId_withoutParam_found() {
    // given
    String linkedId = TYPE.name();

    // when
    Optional<TelegraphType> parsed = TelegraphType.parseByLinkedId(linkedId);

    // then
    assertTrue(parsed.isPresent());
    assertEquals(TYPE, parsed.get());
  }

  @Test
  public void parseByLinkedId_withParam_found() {
    // given
    String linkedId = TYPE.name() + ":43";

    // when
    Optional<TelegraphType> parsed = TelegraphType.parseByLinkedId(linkedId);

    // then
    assertTrue(parsed.isPresent());
    assertEquals(TYPE, parsed.get());
  }

  @Test
  public void parseByLinkedId_withParams_found() {
    // given
    String linkedId = TYPE.name() + ":43:qba";

    // when
    Optional<TelegraphType> parsed = TelegraphType.parseByLinkedId(linkedId);

    // then
    assertTrue(parsed.isPresent());
    assertEquals(TYPE, parsed.get());
  }

  @Test
  public void parseByLinkedId_anotherType_withoutParam_found() {
    // given
    TelegraphType type = TelegraphType.STATISTIC;
    String linkedId = type.name();

    // when
    Optional<TelegraphType> parsed = TelegraphType.parseByLinkedId(linkedId);

    // then
    assertTrue(parsed.isPresent());
    assertEquals(type, parsed.get());
  }

  @Test
  public void parseByLinkedId_anotherType_withParam_found() {
    // given
    TelegraphType type = TelegraphType.STATISTIC;
    String linkedId = type + ":43";

    // when
    Optional<TelegraphType> parsed = TelegraphType.parseByLinkedId(linkedId);

    // then
    assertTrue(parsed.isPresent());
    assertEquals(type, parsed.get());
  }

  @Test
  public void parseByLinkedId_anotherType_withParams_found() {
    // given
    TelegraphType type = TelegraphType.STATISTIC;
    String linkedId = type + ":43:qba";

    // when
    Optional<TelegraphType> parsed = TelegraphType.parseByLinkedId(linkedId);

    // then
    assertTrue(parsed.isPresent());
    assertEquals(type, parsed.get());
  }


  @Test
  public void parseByLinkedId_withoutParam_missingDelimiter() {
    // given
    String linkedId = TYPE + "23";

    // when
    Optional<TelegraphType> parsed = TelegraphType.parseByLinkedId(linkedId);

    // then
    assertFalse(parsed.isPresent());
  }

  @Test
  public void parseByLinkedId_withoutParam_notFound() {
    // given
    String linkedId = "NOT_FOUND";

    // when
    Optional<TelegraphType> parsed = TelegraphType.parseByLinkedId(linkedId);

    // then
    assertFalse(parsed.isPresent());
  }

  @Test
  public void parseByLinkedId_withParam_notFound() {
    // given
    String linkedId = "NOT_FOUND:43";

    // when
    Optional<TelegraphType> parsed = TelegraphType.parseByLinkedId(linkedId);

    // then
    assertFalse(parsed.isPresent());
  }

  @Test
  public void parseByLinkedId_withParams_notFound() {
    // given
    String linkedId = "NOT_FOUND:43:qba";

    // when
    Optional<TelegraphType> parsed = TelegraphType.parseByLinkedId(linkedId);

    // then
    assertFalse(parsed.isPresent());
  }
}
