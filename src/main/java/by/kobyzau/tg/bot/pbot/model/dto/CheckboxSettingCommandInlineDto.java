package by.kobyzau.tg.bot.pbot.model.dto;

import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class CheckboxSettingCommandInlineDto extends SerializableInlineObject {

  @JsonProperty("t")
  private ChatSettingsService.ChatCheckboxSettingType type;

  public CheckboxSettingCommandInlineDto() {
    super(SerializableInlineType.SETTING_ROOT);
  }

  public CheckboxSettingCommandInlineDto(String id, ChatSettingsService.ChatCheckboxSettingType type) {
    super(id, SerializableInlineType.SETTING_ROOT);
    this.type = type;
  }

  public ChatSettingsService.ChatCheckboxSettingType getType() {
    return type;
  }

  public void setType(ChatSettingsService.ChatCheckboxSettingType type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CheckboxSettingCommandInlineDto that = (CheckboxSettingCommandInlineDto) o;
    return type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }
}
