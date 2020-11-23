package by.kobyzau.tg.bot.pbot.program.logger;

import java.util.Arrays;
import java.util.Optional;

public enum LoggerLevel {
  ERROR(null),
  WARN(ERROR),
  INFO(WARN),
  DEBUG(INFO);

  private final LoggerLevel parent;

  LoggerLevel(LoggerLevel parent) {
    this.parent = parent;
  }

  public boolean matchLevel(LoggerLevel level) {
    if (level == null) {
      return false;
    }
    if (this == level) {
      return true;
    }
    if (this.parent == null) {
      return false;
    }
    return this.parent.matchLevel(level);
  }

  public static Optional<LoggerLevel> parseLevel(String name) {
    if (name == null) {
      return Optional.empty();
    }
    String formattedName = name.trim().toUpperCase();
    return Arrays.stream(values())
        .filter(l -> l.name().equalsIgnoreCase(formattedName))
        .findFirst();
  }
}
