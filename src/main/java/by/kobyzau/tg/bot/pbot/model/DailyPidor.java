package by.kobyzau.tg.bot.pbot.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "daily_pidor")
public class DailyPidor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long playerTgId;
  private long chatId;
  private Long caller;
  private LocalDate localDate;

  public DailyPidor() {}

  public DailyPidor(long playerTgId, long chatId, LocalDate localDate) {
    this.playerTgId = playerTgId;
    this.chatId = chatId;
    this.localDate = localDate;
  }

  public DailyPidor(long playerTgId, long chatId, LocalDate localDate, Long caller) {
    this.playerTgId = playerTgId;
    this.chatId = chatId;
    this.localDate = localDate;
    this.caller = caller;
  }

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

  public Long getCaller() {
    return caller;
  }

  public void setCaller(Long caller) {
    this.caller = caller;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DailyPidor that = (DailyPidor) o;
    return id == that.id
        && playerTgId == that.playerTgId
        && chatId == that.chatId
        && Objects.equals(caller, that.caller)
        && Objects.equals(localDate, that.localDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, playerTgId, chatId, caller, localDate);
  }


  @Override
  public String toString() {
    return "DailyPidor{" +
        "id=" + id +
        ", playerTgId=" + playerTgId +
        ", chatId=" + chatId +
        ", caller=" + caller +
        ", localDate=" + localDate +
        '}';
  }

}
