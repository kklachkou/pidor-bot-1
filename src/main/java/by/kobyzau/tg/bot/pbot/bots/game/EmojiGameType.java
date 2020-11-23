package by.kobyzau.tg.bot.pbot.bots.game;

public enum EmojiGameType {
  DICE("Игра в кубик"),
  DARTS("Игра в дартс"),
  BASKETBALL("Игра в баскетбол"),
  FOOTBALL("Игра в футбол");

  private final String gameName;

  EmojiGameType(String gameName) {
    this.gameName = gameName;
  }

  public String getGameName() {
    return gameName;
  }
}
