package by.kobyzau.tg.bot.pbot.program.logger;

import org.springframework.stereotype.Component;

@Component("SystemLogger")
public class SystemLogger implements Logger {

  private static final org.apache.log4j.Logger log =
      org.apache.log4j.Logger.getLogger(SystemLogger.class);

  @Override
  public void debug(String message) {
    log.debug(message);
  }

  @Override
  public void info(String message) {
    log.info(message);
  }

  @Override
  public void warn(String message) {
    log.warn(message);
  }

  @Override
  public void error(String s, Exception e) {
    log.error(s, e);
  }
}
