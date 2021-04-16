package by.kobyzau.tg.bot.pbot.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "exclude_game")
public class ExcludeGameUserValue {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long playerTgId;
  private long chatId;
  private LocalDate localDate;

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
}
