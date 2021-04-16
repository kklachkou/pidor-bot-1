package by.kobyzau.tg.bot.pbot.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class VoteInlineMessageInlineDto extends SerializableInlineObject {

  @JsonProperty("t")
  private Long targetId;

  public VoteInlineMessageInlineDto() {
    super(SerializableInlineType.VOTE);
  }

  public VoteInlineMessageInlineDto(String id, Long targetUserId) {
    super(id, SerializableInlineType.VOTE);
    this.targetId = targetUserId;
  }

  public Long getTargetId() {
    return targetId;
  }

  public void setTargetId(Long tId) {
    this.targetId = tId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    VoteInlineMessageInlineDto dto = (VoteInlineMessageInlineDto) o;
    return Objects.equals(targetId, dto.targetId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), targetId);
  }
}
