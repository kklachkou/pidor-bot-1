package by.kobyzau.tg.bot.pbot.model;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
//@Table(name = "questionnaire_answer")
public class QuestionnaireAnswer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long chatId;
  private long playerTgId;
  private QuestionnaireType type;
  private int option;
  private LocalDate date;

  public QuestionnaireAnswer() {}

  public QuestionnaireAnswer(
      long chatId, long playerTgId, QuestionnaireType type, int option, LocalDate date) {
    this.chatId = chatId;
    this.playerTgId = playerTgId;
    this.type = type;
    this.option = option;
    this.date = date;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getChatId() {
    return chatId;
  }

  public void setChatId(long chatId) {
    this.chatId = chatId;
  }

  public long getPlayerTgId() {
    return playerTgId;
  }

  public void setPlayerTgId(long playerTgId) {
    this.playerTgId = playerTgId;
  }

  public QuestionnaireType getType() {
    return type;
  }

  public void setType(QuestionnaireType type) {
    this.type = type;
  }

  public int getOption() {
    return option;
  }

  public void setOption(int option) {
    this.option = option;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }
}
