package by.kobyzau.tg.bot.pbot.model;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pidor_yearly_stat")
public class PidorYearlyStat {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long playerTgId;
  private long chatId;
  private int year;
  private int count;
  private LocalDate lastDate;


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

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public LocalDate getLastDate() {
    return lastDate;
  }

  public void setLastDate(LocalDate lastDate) {
    this.lastDate = lastDate;
  }
}
