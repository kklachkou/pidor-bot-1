package by.kobyzau.tg.bot.pbot.model.dto;

import by.kobyzau.tg.bot.pbot.model.FeedbackEmojiType;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class FeedbackInlineDto extends SerializableInlineObject {

  @JsonProperty("e")
  private FeedbackEmojiType emojiType;
  @JsonProperty("t")
  private FeedbackType type;

  public FeedbackInlineDto() {
    super(SerializableInlineType.FEEDBACK);
  }

  public FeedbackInlineDto(String id,FeedbackType feedbackType,  FeedbackEmojiType emojiType) {
    super(id, SerializableInlineType.FEEDBACK);
    this.emojiType = emojiType;
    this.type = feedbackType;
  }

  public FeedbackEmojiType getEmojiType() {
    return emojiType;
  }

  public void setEmojiType(FeedbackEmojiType emojiType) {
    this.emojiType = emojiType;
  }

  public FeedbackType getType() {
    return type;
  }

  public void setType(FeedbackType type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FeedbackInlineDto that = (FeedbackInlineDto) o;
    return emojiType == that.emojiType &&
            type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), emojiType, type);
  }
}
