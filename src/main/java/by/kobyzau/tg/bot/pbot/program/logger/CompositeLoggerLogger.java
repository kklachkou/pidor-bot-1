package by.kobyzau.tg.bot.pbot.program.logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Primary
public class CompositeLoggerLogger implements Logger {

  private List<Logger> loggers = new ArrayList<>();

  @Autowired
  @Qualifier("SystemLogger")
  private Logger systemLogger;

  @Autowired
  @Qualifier("TGLogger")
  private Logger tgLogger;

  @Autowired private LoggerLevelHolder loggerLevel;

  @PostConstruct
  private void init() {
    loggers.add(systemLogger);
    loggers.add(tgLogger);
  }

  @Override
  public void debug(String message) {
    if (loggerLevel.getLevel().matchLevel(LoggerLevel.DEBUG)) {
      loggers.forEach(logger -> logger.debug(message));
    }
  }

  @Override
  public void info(String message) {
    if (loggerLevel.getLevel().matchLevel(LoggerLevel.INFO)) {
      loggers.forEach(logger -> logger.info(message));
    }
  }

  @Override
  public void warn(String message) {
    if (loggerLevel.getLevel().matchLevel(LoggerLevel.WARN)) {
      loggers.forEach(logger -> logger.warn(message));
    }
  }

  @Override
  public void error(String s, Exception e) {
    if (loggerLevel.getLevel().matchLevel(LoggerLevel.ERROR)) {
      loggers.forEach(logger -> logger.error(s, e));
    }
  }
}
