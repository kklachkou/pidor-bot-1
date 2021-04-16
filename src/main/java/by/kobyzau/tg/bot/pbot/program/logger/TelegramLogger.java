package by.kobyzau.tg.bot.pbot.program.logger;

import by.kobyzau.tg.bot.pbot.tg.TelegramSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component("TGLogger")
public class TelegramLogger implements Logger {

  @Value("${logger.tg.token}")
  private String botToken;

  @Value("${logger.tg.chat}")
  private String backupChat;

  @Autowired private LoggerLevelHolder loggerLevel;

  @Autowired private TelegramSender telegramSender;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @Override
  public void debug(String message) {
    if (loggerLevel.getLevel().matchLevel(LoggerLevel.DEBUG)) {
      executor.execute(() -> sendMessage("<pre>[DEBUG]</pre>\n" + message));
    }
  }

  @Override
  public void info(String message) {
    if (loggerLevel.getLevel().matchLevel(LoggerLevel.INFO)) {
      executor.execute(() -> sendMessage("<pre>[INFO]</pre>\n" + message));
    }
  }

  @Override
  public void warn(String message) {
    if (loggerLevel.getLevel().matchLevel(LoggerLevel.WARN)) {
      executor.execute(() -> sendMessage("[ #WARNING ]\n⚠️⚠️⚠️⚠️⚠️\n" + message));
    }
  }

  @Override
  public void error(String s, Exception e) {
    if (loggerLevel.getLevel().matchLevel(LoggerLevel.ERROR)) {
      executor.execute(
          () ->
              sendMessage(
                  "[ #ERROR ]\n❗️❗️❗️❗️❗️\n"
                      + s
                      + "\n"
                      + e.getMessage()
                      + "\nStacktrace:\n<pre>"
                      + getStacktrace(e)
                      + "</pre>"));
    }
  }

  private String getStacktrace(Exception e) {
    try {
      StackTraceElement[] stackTrace = e.getStackTrace();
      int skippedLines = 0;
      StringBuilder sb = new StringBuilder();
      for (StackTraceElement stackTraceElement : stackTrace) {
        if (stackTraceElement.getClassName().contains("kobyzau")) {
          if (skippedLines > 0) {
            sb.append("\n...").append(skippedLines).append(" lines skipped...");
            skippedLines = 0;
          }
          sb.append("\n")
              .append(stackTraceElement.getClassName())
              .append(".")
              .append(stackTraceElement.getMethodName())
              .append(":")
              .append(stackTraceElement.getLineNumber());
        } else {
          skippedLines++;
        }
      }
      if (skippedLines > 0) {
        sb.append("\n...").append(skippedLines).append(" lines skipped...");
      }
      return sb.toString();
    } catch (Exception stExc) {
      return "{not available}";
    }
  }

  private void sendMessage(String message) {
    try {
      telegramSender.sendMessage(botToken, backupChat, message);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
