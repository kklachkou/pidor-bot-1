package by.kobyzau.tg.bot.pbot.handlers.update.schedule;

public enum ScheduledItem {
  //MONDAY, THURSDAY
  EMOJI_GAME(false),
  //SATURDAY
  EXCLUDE_GAME(false),
  //Every 13 days
  POLL(true),
  //SUNDAY every 2 times
  EDITED_MESSAGE(true),
  //WEDNESDAY
  ASSASSIN(true),
  NONE(true);

  private final boolean requireManualStart;

  ScheduledItem(boolean requireManualStart) {
    this.requireManualStart = requireManualStart;
  }

  public boolean isRequireManualStart() {
    return requireManualStart;
  }
}
