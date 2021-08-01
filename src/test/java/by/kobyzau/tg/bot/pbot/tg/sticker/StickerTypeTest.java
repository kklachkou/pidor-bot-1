package by.kobyzau.tg.bot.pbot.tg.sticker;

import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.matchers.LessOrEqual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StickerTypeTest {

  @Test
  public void assertDuplication() {
    Set<String> uniqueStickers = new HashSet<>();
    for (StickerType stickerType : EnumSet.allOf(StickerType.class)) {
      List<String> stickers = stickerType.getStickers();
      for (String sticker : stickers) {
        if (!uniqueStickers.add(sticker)) {
          fail(
              new ParametizedText(
                      "Duplicate Sticker for Type {0} - {1}",
                      new SimpleText(stickerType.name()), new SimpleText(sticker))
                  .text());
        }
      }
    }
  }

  @Test
  public void assertStickerNameLength() {
    for (StickerType sticker : StickerType.values()) {
      Assert.assertThat(sticker.name().length(), new LessOrEqual<>(30));
    }
  }

  @Test
  public void parseSticker_undefined_empty() {
    // given
    String name = "fadsr213";

    // when
    Optional<StickerType> stickerType = StickerType.parseSticker(name);

    // then
    assertFalse(stickerType.isPresent());
  }

  @Test
  public void parseSticker_lol_present() {
    // given
    String name = "lol";

    // when
    Optional<StickerType> stickerType = StickerType.parseSticker(name);

    // then
    assertTrue(stickerType.isPresent());
    assertEquals(StickerType.LOL, stickerType.get());
  }

  @Test
  public void getPidorSticker_undefined_empty() {
    // given
    String name = "fadsr213";

    // when
    Optional<StickerType> stickerType = StickerType.getPidorSticker(name);

    // then
    assertFalse(stickerType.isPresent());
  }

  @Test
  public void getPidorSticker_lol_empty() {
    // given
    String name = "lol";

    // when
    Optional<StickerType> stickerType = StickerType.getPidorSticker(name);

    // then
    assertFalse(stickerType.isPresent());
  }

  @Test
  public void getPidorSticker_mikita_present() {
    // given
    String name = "pidor_NIKITA";

    // when
    Optional<StickerType> stickerType = StickerType.getPidorSticker(name);

    // then
    assertTrue(stickerType.isPresent());
    assertEquals(StickerType.PIDOR_NIKITA, stickerType.get());
  }
}
