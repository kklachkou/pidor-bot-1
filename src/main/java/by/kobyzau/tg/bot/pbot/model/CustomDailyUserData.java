package by.kobyzau.tg.bot.pbot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "custom_daily_user_data")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomDailyUserData {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long playerTgId;
  private long chatId;
  private LocalDate localDate;
  private Type type;
  private String data;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getPlayerTgId() {
    return playerTgId;
  }

  public void setPlayerTgId(long playerTgId) {
    this.playerTgId = playerTgId;
  }

  public long getChatId() {
    return chatId;
  }

  public void setChatId(long chatId) {
    this.chatId = chatId;
  }

  public LocalDate getLocalDate() {
    return localDate;
  }

  public void setLocalDate(LocalDate localDate) {
    this.localDate = localDate;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public enum Type {
    ELECTION_VOTE,FUTURE_ACTION, CHAT_CHECKBOX_SETTING, SUPER_ELECTION_VOTE
  }
}
