package by.kobyzau.tg.bot.pbot.program.logger;

public interface Logger {

  void debug(String message);

  void info(String message);

  void warn(String message);

  void error(String s, Exception e);
}
