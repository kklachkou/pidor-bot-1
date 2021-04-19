package by.kobyzau.tg.bot.pbot.model;

public enum FeedbackEmojiType {
  LAUGH("\uD83D\uDE02"),
  LOVE("❤️"),
  CRY("\uD83D\uDE2D"),
  EGGPLANT("\uD83C\uDF46"),
  COCK("\uD83D\uDC13"),
  ;

  private final String emoji;

  FeedbackEmojiType(String emoji) {
    this.emoji = emoji;
  }

  public String getEmoji() {
    return emoji;
  }
}
