package by.kobyzau.tg.bot.pbot.model.dto;

import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GdprMessageDtoTest {

  @Test
  public void deserialize_eq() {
    // given
    GdprMessageDto dto = new GdprMessageDto(5345L, 345);

    // when
    String json = StringUtil.serialize(dto);
    Optional<GdprMessageDto> deserialized = StringUtil.deserialize(json, GdprMessageDto.class);

    // then
    assertTrue(deserialized.isPresent());
    assertEquals(dto, deserialized.get());
  }
}
