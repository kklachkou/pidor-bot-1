package by.kobyzau.tg.bot.pbot.model;

import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNamePidorText;
import by.kobyzau.tg.bot.pbot.util.StringUtil;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "pidor")
public class Pidor{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private int tgId;
  private long chatId;
  private String username;
  private String fullname;
  private String nickname;
  private LocalDate usernameLastUpdated;
  private String sticker;

  public Pidor() {}

  public Pidor(int tgId, long chatId, String fullname) {
    this.tgId = tgId;
    this.chatId = chatId;
    this.fullname = fullname;
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

  public int getTgId() {
    return tgId;
  }

  public void setTgId(int tgId) {
    this.tgId = tgId;
  }

  public String getSticker() {
    if (StringUtil.isNotBlank(sticker)) {
      return sticker;
    }
    return EggsPidor.parseById(getTgId())
        .map(EggsPidor::getStickerType)
        .map(Enum::name)
        .orElse(null);
  }

  public void setSticker(String sticker) {
    this.sticker = sticker;
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
