package by.kobyzau.tg.bot.pbot.model.dto;

import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.junit.Test;
import org.mockito.internal.matchers.LessOrEqual;

import java.util.Optional;
import java.util.UUID;

import static by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType.VOTE;
import static org.junit.Assert.*;

public class VoteInlineMessageDtoTest {

  @Test
  public void serizlize_maxLength64() {
    // given
    VoteInlineMessageDto dtoWithNegative =
        new VoteInlineMessageDto(
            UUID.randomUUID().toString().substring(VOTE.getIdSize()),
            Integer.MIN_VALUE,
            Integer.MIN_VALUE);
    VoteInlineMessageDto dtoWithPositive =
        new VoteInlineMessageDto(
            UUID.randomUUID().toString().substring(VOTE.getIdSize()), Integer.MAX_VALUE, Integer.MAX_VALUE);

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
    VoteInlineMessageDto dto =
        new VoteInlineMessageDto(UUID.randomUUID().toString().substring(VOTE.getIdSize()), 53, 24);

    // when
    String json = StringUtil.serialize(dto);
    Optional<VoteInlineMessageDto> deserialized =
        StringUtil.deserialize(json, VoteInlineMessageDto.class);

    // then
    assertTrue(deserialized.isPresent());
    assertEquals(dto, deserialized.get());
    assertEquals(VOTE.getIndex(), deserialized.get().getIndex());
  }
}
