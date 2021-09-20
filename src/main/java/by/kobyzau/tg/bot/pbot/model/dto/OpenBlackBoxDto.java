package by.kobyzau.tg.bot.pbot.model.dto;

public class OpenBlackBoxDto extends SerializableInlineObject {

  public OpenBlackBoxDto() {
    super(SerializableInlineType.OPEN_BLACK_BOX);
  }

  public OpenBlackBoxDto(String id) {
    super(id, SerializableInlineType.OPEN_BLACK_BOX);
  }
}
