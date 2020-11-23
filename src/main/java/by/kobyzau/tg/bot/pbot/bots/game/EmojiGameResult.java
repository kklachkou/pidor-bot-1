package by.kobyzau.tg.bot.pbot.bots.game;

public enum EmojiGameResult {
  //dice symbol
  NONE("\uD83C\uDFB2"),
  LOSE( "✅"),
  WIN("\uD83C\uDFC6"),
  DRAW("☑️");

  private final String symbol;

  EmojiGameResult(String symbol) {
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }
}
