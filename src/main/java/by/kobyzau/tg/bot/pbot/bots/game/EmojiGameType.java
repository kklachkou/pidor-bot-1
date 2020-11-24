package by.kobyzau.tg.bot.pbot.bots.game;

public enum EmojiGameType {
  DICE("Игра в кубик", "\ud83c\udfb2"),
  DARTS("Игра в дартс", "\ud83c\udfaf"),
  BASKETBALL("Игра в баскетбол", "\ud83c\udfc0"),
  FOOTBALL("Игра в футбол","⚽");

  private final String gameName;
  private final String emoji;

  EmojiGameType(String gameName, String emoji) {
    this.gameName = gameName;
    this.emoji = emoji;
  }

  public String getGameName() {
    return gameName;
  }

  public String getEmoji() {
    return emoji;
  }
}
