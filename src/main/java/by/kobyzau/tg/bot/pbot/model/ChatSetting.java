package by.kobyzau.tg.bot.pbot.model;

import by.kobyzau.tg.bot.pbot.model.type.GameFrequent;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "chat_setting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChatSetting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long chatId;
  private boolean autoRegisterUsers;
  private boolean electionAnonymous;
  private GameFrequent emojiGameFrequent;
  private GameFrequent electionFrequent;
  private LocalDate created;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChatSetting that = (ChatSetting) o;
    return id == that.id && chatId == that.chatId && autoRegisterUsers == that.autoRegisterUsers && electionAnonymous == that.electionAnonymous && emojiGameFrequent == that.emojiGameFrequent && electionFrequent == that.electionFrequent && Objects.equals(created, that.created);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, chatId, autoRegisterUsers, electionAnonymous, emojiGameFrequent, electionFrequent, created);
  }
}
