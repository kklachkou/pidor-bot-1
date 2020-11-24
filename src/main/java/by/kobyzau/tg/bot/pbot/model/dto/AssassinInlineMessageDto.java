package by.kobyzau.tg.bot.pbot.model.dto;

public class AssassinInlineMessageDto {

  private String id;
  private Integer tId;
  private Integer cId;

  public AssassinInlineMessageDto() {
  }

  public AssassinInlineMessageDto(String id, Integer targetUserId, Integer calledUserId) {
    this.tId = targetUserId;
    this.cId = calledUserId;
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer gettId() {
    return tId;
  }

  public void settId(Integer tId) {
    this.tId = tId;
  }

  public Integer getcId() {
    return cId;
  }

  public void setcId(Integer cId) {
    this.cId = cId;
  }
}
