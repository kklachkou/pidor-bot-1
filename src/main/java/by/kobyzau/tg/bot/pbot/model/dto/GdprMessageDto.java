package by.kobyzau.tg.bot.pbot.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class GdprMessageDto {

  @JsonProperty("c")
  private Long chatId;
  @JsonProperty("m")
  private Integer messageId;

  public GdprMessageDto() {}

  public GdprMessageDto(Long cId, Integer mId) {
    this.chatId = cId;
    this.messageId = mId;
  }

  public Long getChatId() {
    return chatId;
  }

  public void setChatId(Long chatId) {
    this.chatId = chatId;
  }

  public Integer getMessageId() {
    return messageId;
  }

  public void setMessageId(Integer messageId) {
    this.messageId = messageId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GdprMessageDto dto = (GdprMessageDto) o;
    return Objects.equals(chatId, dto.chatId) &&
            Objects.equals(messageId, dto.messageId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(chatId, messageId);
  }
}
