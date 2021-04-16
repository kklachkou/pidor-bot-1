package by.kobyzau.tg.bot.pbot.model.dto;

import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.junit.Test;
import org.mockito.internal.matchers.LessOrEqual;

import java.util.Optional;
import java.util.UUID;

import static by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType.VOTE;
import static org.junit.Assert.*;

public class VoteInlineMessageInlineDtoTest {

  @Test
  public void serizlize_maxLength64() {
    // given
    VoteInlineMessageInlineDto dtoWithNegative =
        new VoteInlineMessageInlineDto(
                UUID.randomUUID().toString().substring(VOTE.getIdSize()),
            Long.MIN_VALUE);
    VoteInlineMessageInlineDto dtoWithPositive =
        new VoteInlineMessageInlineDto(
                UUID.randomUUID().toString().substring(VOTE.getIdSize()), Long.MAX_VALUE);

    // when
    String serializeNegative = StringUtil.serialize(dtoWithNegative);
    String serializePositive = StringUtil.serialize(dtoWithPositive);

    // then
    assertThat(
        "JSON: \n" + serializeNegative, serializeNegative.getBytes().length, new LessOrEqual<>(64));
    assertThat(
        "JSON: \n" + serializePositive, serializePositive.getBytes().length, new LessOrEqual<>(64));
  }

  @Test
  public void deserialize_eq() {
    // given
    VoteInlineMessageInlineDto dto =
        new VoteInlineMessageInlineDto(UUID.randomUUID().toString().substring(VOTE.getIdSize()), 53L);

    // when
    String json = StringUtil.serialize(dto);
    Optional<VoteInlineMessageInlineDto> deserialized =
        StringUtil.deserialize(json, VoteInlineMessageInlineDto.class);

    // then
    assertTrue(deserialized.isPresent());
    assertEquals(dto, deserialized.get());
    assertEquals(VOTE.getIndex(), deserialized.get().getIndex());
  }
}
