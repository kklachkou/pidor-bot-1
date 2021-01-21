package by.kobyzau.tg.bot.pbot.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class VoteInlineMessageDto extends SerializableInlineObject {


  @JsonProperty("t")
  private Integer targetId;
  @JsonProperty("c")
  private Integer callerId;

  public VoteInlineMessageDto() {
    super(SerializableInlineType.VOTE);
  }

  public VoteInlineMessageDto(String id, Integer targetUserId, Integer calledUserId) {
    super(id, SerializableInlineType.VOTE);
    this.targetId = targetUserId;
    this.callerId = calledUserId;
  }

  public Integer getTargetId() {
    return targetId;
  }

  public void setTargetId(Integer tId) {
    this.targetId = tId;
  }

  public Integer getCallerId() {
    return callerId;
  }

  public void setCallerId(Integer cId) {
    this.callerId = cId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    VoteInlineMessageDto dto = (VoteInlineMessageDto) o;
    return Objects.equals(targetId, dto.targetId) &&
            Objects.equals(callerId, dto.callerId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), targetId, callerId);
  }
}
