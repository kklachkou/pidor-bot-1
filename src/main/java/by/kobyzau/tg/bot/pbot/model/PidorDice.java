package by.kobyzau.tg.bot.pbot.model;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pidor_dice")
public class PidorDice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private int playerTgId;
  private long chatId;
  private LocalDate localDate;
  private int value;

  public PidorDice() {}

  public PidorDice(int tgId, long chatId, LocalDate localDate, int value) {
    this.playerTgId = tgId;
    this.chatId = chatId;
    this.localDate = localDate;
    this.value = value;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getPlayerTgId() {
    return playerTgId;
  }

  public void setPlayerTgId(int playerTgId) {
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

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PidorDice pidorDice = (PidorDice) o;
    return id == pidorDice.id
        && playerTgId == pidorDice.playerTgId
        && chatId == pidorDice.chatId
        && value == pidorDice.value
        && Objects.equals(localDate, pidorDice.localDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, playerTgId, chatId, localDate, value);
  }

  @Override
  public String toString() {
    return "PidorDice{"
        + "id="
        + id
        + ", playerTgId="
        + playerTgId
        + ", chatId="
        + chatId
        + ", localDate="
        + localDate
        + ", value="
        + value
        + '}';
  }
}
