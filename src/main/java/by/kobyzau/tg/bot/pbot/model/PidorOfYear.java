package by.kobyzau.tg.bot.pbot.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "pidor_of_year")
public class PidorOfYear {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private int playerTgId;
  private long chatId;
  private int year;

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

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PidorOfYear that = (PidorOfYear) o;
    return id == that.id && playerTgId == that.playerTgId && chatId == that.chatId && year == that.year;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, playerTgId, chatId, year);
  }
}
