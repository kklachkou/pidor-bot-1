package by.kobyzau.tg.bot.pbot.handlers.update.schedule;

public enum ScheduledItem {
  //MONDAY, WEDNESDAY, THURSDAY, FRIDAY
  EMOJI_GAME(false),
  //SATURDAY
  EXCLUDE_GAME(false),
  //SUNDAY
  ELECTION(false),
  //TUESDAY, SATURDAY every 2 times
  POTATOES(false),
  NONE(true);

  private final boolean requireManualStart;

  ScheduledItem(boolean requireManualStart) {
    this.requireManualStart = requireManualStart;
  }

  public boolean isRequireManualStart() {
    return requireManualStart;
  }
}
