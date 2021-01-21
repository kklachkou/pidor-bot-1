package by.kobyzau.tg.bot.pbot.model.dto;

public class CloseInlineMessageInlineDto extends SerializableInlineObject {

  public CloseInlineMessageInlineDto() {
    super(SerializableInlineType.CLOSE_INLINE);
  }

  public CloseInlineMessageInlineDto(String id) {
    super(id, SerializableInlineType.CLOSE_INLINE);
  }
}
