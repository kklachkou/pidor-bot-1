package by.kobyzau.tg.bot.pbot.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "potato_taker")
public class HotPotatoTaker {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long playerTgId;
  private long chatId;
  private LocalDate date;
  private LocalDateTime deadline;

  public HotPotatoTaker() {}

  public HotPotatoTaker(HotPotatoTaker hotPotatoTaker) {
    this.id = hotPotatoTaker.id;
    this.playerTgId = hotPotatoTaker.playerTgId;
    this.chatId = hotPotatoTaker.chatId;
    this.date = hotPotatoTaker.date;
    this.deadline = hotPotatoTaker.deadline;
  }

  public HotPotatoTaker(long playerTgId, long chatId, LocalDate date, LocalDateTime deadline) {
    this.playerTgId = playerTgId;
    this.chatId = chatId;
    this.date = date;
    this.deadline = deadline;
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

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LocalDateTime getDeadline() {
    return deadline;
  }

  public void setDeadline(LocalDateTime deadline) {
    this.deadline = deadline;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HotPotatoTaker that = (HotPotatoTaker) o;
    return id == that.id &&
            playerTgId == that.playerTgId &&
            chatId == that.chatId &&
            Objects.equals(date, that.date) &&
            Objects.equals(deadline, that.deadline);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, playerTgId, chatId, date, deadline);
  }
}
