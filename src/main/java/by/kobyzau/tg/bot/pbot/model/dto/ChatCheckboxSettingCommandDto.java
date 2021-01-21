package by.kobyzau.tg.bot.pbot.model.dto;

import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ChatCheckboxSettingCommandDto {

  @JsonProperty("c")
  private Long chatId;

  @JsonProperty("t")
  private ChatSettingsService.ChatCheckboxSettingType type;


  @JsonProperty("e")
  private Boolean enable;

  public ChatCheckboxSettingCommandDto() {}

  public ChatCheckboxSettingCommandDto(Long chatId, ChatSettingsService.ChatCheckboxSettingType type, Boolean enable) {
    this.chatId = chatId;
    this.type = type;
    this.enable = enable;
  }

  public Long getChatId() {
    return chatId;
  }

  public void setChatId(Long chatId) {
    this.chatId = chatId;
  }

  public ChatSettingsService.ChatCheckboxSettingType getType() {
    return type;
  }

  public void setType(ChatSettingsService.ChatCheckboxSettingType type) {
    this.type = type;
  }

  public Boolean getEnable() {
    return enable;
  }

  public void setEnable(Boolean enable) {
    this.enable = enable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChatCheckboxSettingCommandDto that = (ChatCheckboxSettingCommandDto) o;
    return Objects.equals(chatId, that.chatId) &&
            type == that.type &&
            Objects.equals(enable, that.enable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(chatId, type, enable);
  }
}
