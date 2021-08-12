package by.kobyzau.tg.bot.pbot.model.dto;

public class HotPotatoDto extends SerializableInlineObject {

  public HotPotatoDto() {
    super(SerializableInlineType.HOT_POTATOES);
  }

  public HotPotatoDto(String id) {
    super(id, SerializableInlineType.HOT_POTATOES);
  }
}
