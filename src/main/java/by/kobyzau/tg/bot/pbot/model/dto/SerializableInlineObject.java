package by.kobyzau.tg.bot.pbot.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public abstract class SerializableInlineObject {

  @JsonProperty("id")
  private String id;

  @JsonProperty("i")
  private int index;

  public SerializableInlineObject(SerializableInlineType type) {
    this.index = type.getIndex();
  }

  public SerializableInlineObject(String id, SerializableInlineType type) {
    this.id = id;
    this.index = type.getIndex();
  }

  public void setIndex(int i) {
    this.index = i;
  }

  public int getIndex() {
    return index;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SerializableInlineObject that = (SerializableInlineObject) o;
    return index == that.index && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, index);
  }
}
