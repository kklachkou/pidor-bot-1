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

  @PostConstruct
  private void init() {
    loggers.add(systemLogger);
    loggers.add(tgLogger);
  }

  @Override
  public void debug(String message) {
    loggers.forEach(logger -> logger.debug(message));
  }

  @Override
  public void info(String message) {
    loggers.forEach(logger -> logger.info(message));
  }

  @Override
  public void warn(String message) {
    loggers.forEach(logger -> logger.warn(message));
  }

  @Override
  public void error(String s, Exception e) {
    loggers.forEach(logger -> logger.error(s, e));
  }
}
