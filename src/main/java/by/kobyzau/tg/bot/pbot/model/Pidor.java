package by.kobyzau.tg.bot.pbot.model;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "pidor")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pidor{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long tgId;
  private long chatId;
  private String username;
  private String fullname;
  private String nickname;
  private LocalDate usernameLastUpdated;
  private String sticker;

  @Transient
  private List<PidorMark> pidorMarks;
  @Transient
  private Set<ArtifactType> artifacts;

  public Pidor(long tgId, long chatId, String fullname) {
    this.tgId = tgId;
    this.chatId = chatId;
    this.fullname = fullname;
  }

  public Pidor(long tgId, long chatId, String fullname, List<PidorMark> pidorMarks) {
    this.tgId = tgId;
    this.chatId = chatId;
    this.fullname = fullname;
    this.pidorMarks = pidorMarks;
  }

  public static Pidor unknown(long chatId) {
    return new Pidor(0, chatId, "Покинувший чат");
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFullName() {
    return fullname;
  }

  public void setFullName(String fullName) {
    this.fullname = fullName;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public LocalDate getUsernameLastUpdated() {
    return usernameLastUpdated;
  }

  public void setUsernameLastUpdated(LocalDate usernameLastUpdated) {
    this.usernameLastUpdated = usernameLastUpdated;
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

  public long getTgId() {
    return tgId;
  }

  public void setTgId(long tgId) {
    this.tgId = tgId;
  }

  public String getSticker() {
    return sticker;
  }

  public void setSticker(String sticker) {
    this.sticker = sticker;
  }

  public List<PidorMark> getPidorMarks() {
    return pidorMarks;
  }

  public void setPidorMarks(List<PidorMark> pidorMarks) {
    this.pidorMarks = pidorMarks;
  }

  public Set<ArtifactType> getArtifacts() {
    return artifacts;
  }

  public void setArtifacts(Set<ArtifactType> artifacts) {
    this.artifacts = artifacts;
  }

  @Override
  public String toString() {
    return new ShortNamePidorText(this) + " chatId=" + chatId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pidor pidor = (Pidor) o;
    return id == pidor.id
        && tgId == pidor.tgId
        && chatId == pidor.chatId
        && Objects.equals(sticker, pidor.sticker)
        && Objects.equals(username, pidor.username)
        && Objects.equals(fullname, pidor.fullname)
        && Objects.equals(nickname, pidor.nickname)
        && Objects.equals(usernameLastUpdated, pidor.usernameLastUpdated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, tgId, chatId, username, fullname, sticker, nickname, usernameLastUpdated);
  }
}
