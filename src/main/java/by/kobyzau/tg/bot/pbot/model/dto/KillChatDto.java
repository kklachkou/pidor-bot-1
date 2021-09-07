package by.kobyzau.tg.bot.pbot.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KillChatDto extends SerializableInlineObject {

  private Long chatId;

  public KillChatDto() {
    super(SerializableInlineType.KILL_CHAT);
  }

  public KillChatDto(Long chatId) {
    this();
    this.chatId = chatId;
  }
}
