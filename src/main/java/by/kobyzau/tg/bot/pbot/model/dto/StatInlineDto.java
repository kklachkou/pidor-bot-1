package by.kobyzau.tg.bot.pbot.model.dto;

import by.kobyzau.tg.bot.pbot.model.StatType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class StatInlineDto extends SerializableInlineObject {

  @JsonProperty("t")
  private StatType type;

  public StatInlineDto() {
    super(SerializableInlineType.STAT);
  }

  public StatInlineDto(String id, StatType type) {
    super(id, SerializableInlineType.STAT);
    this.type = type;
  }


  public StatType getType() {
    return type;
  }

  public void setType(StatType type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StatInlineDto that = (StatInlineDto) o;
    return type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }
}
