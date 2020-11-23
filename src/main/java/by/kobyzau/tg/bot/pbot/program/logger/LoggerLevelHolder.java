package by.kobyzau.tg.bot.pbot.program.logger;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoggerLevelHolder {

  @Value("${logger.tg.level:INFO}")
  private LoggerLevel level;

  public LoggerLevel getLevel() {
    return level;
  }

  public void setLevel(LoggerLevel level) {
    Objects.requireNonNull(level, "Logger level cannot be null");
    this.level = level;
  }
}
