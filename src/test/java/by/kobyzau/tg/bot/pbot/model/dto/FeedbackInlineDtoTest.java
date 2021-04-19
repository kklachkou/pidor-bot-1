package by.kobyzau.tg.bot.pbot.model.dto;

import by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.internal.matchers.LessOrEqual;

import java.util.Optional;
import java.util.UUID;

import static by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType.FEEDBACK;
import static org.junit.Assert.*;

public class FeedbackInlineDtoTest {

  @Test
  public void serizlize_maxLength64() {
    for (FeedbackType feedbackType : FeedbackType.values()) {
      for (FeedbackEmojiType emojiType : FeedbackEmojiType.values()) {
        // given
        FeedbackInlineDto dto =
            new FeedbackInlineDto(
                UUID.randomUUID().toString().substring(FEEDBACK.getIdSize()),
                feedbackType,
                emojiType);

        // when
        String json = StringUtil.serialize(dto);

        // then
        MatcherAssert.assertThat("JSON: \n" + json, json.getBytes().length, new LessOrEqual<>(64));
      }
    }
  }

  @Test
  public void deserialize_eq() {
    // given
    FeedbackInlineDto dto =
        new FeedbackInlineDto(
            UUID.randomUUID().toString().substring(FEEDBACK.getIdSize()),
            FeedbackType.VERSION,
            FeedbackEmojiType.LOVE);

    // when
    String json = StringUtil.serialize(dto);
    Optional<FeedbackInlineDto> deserialized =
        StringUtil.deserialize(json, FeedbackInlineDto.class);

    // then
    assertTrue(deserialized.isPresent());
    assertEquals(dto, deserialized.get());
    assertEquals(FEEDBACK.getIndex(), deserialized.get().getIndex());
  }

  @Test
  public void deserialize_wrongIndex() {
    // given

    // when
    String json = "{\"i\":0}";
    Optional<FeedbackInlineDto> deserialized =
        StringUtil.deserialize(json, FeedbackInlineDto.class);

    // then
    assertTrue(json, deserialized.isPresent());
    assertNotEquals(json,FEEDBACK.getIndex(), deserialized.get().getIndex());
  }
}
