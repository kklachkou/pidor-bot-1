package by.kobyzau.tg.bot.pbot.model.dto;

public class AssassinInlineMessageDto {

  public AssassinInlineMessageDto() {
  }

  public AssassinInlineMessageDto(Integer targetUserId, Integer calledUserId) {
    this.targetUserId = targetUserId;
    this.calledUserId = calledUserId;
  }

  private Integer targetUserId;
  private Integer calledUserId;

  public Integer getTargetUserId() {
    return targetUserId;
  }

  public void setTargetUserId(Integer targetUserId) {
    this.targetUserId = targetUserId;
  }

  public Integer getCalledUserId() {
    return calledUserId;
  }

  public void setCalledUserId(Integer calledUserId) {
    this.calledUserId = calledUserId;
  }
}
