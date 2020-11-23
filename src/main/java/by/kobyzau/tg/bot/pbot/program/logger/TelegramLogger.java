package by.kobyzau.tg.bot.pbot.program.logger;

import by.kobyzau.tg.bot.pbot.tg.TelegramSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("TGLogger")
public class TelegramLogger implements Logger {

  @Value("${logger.tg.bot.token}")
  private String botToken;

  @Value("${logger.tg.chat}")
  private String backupChat;

  @Autowired private LoggerLevelHolder loggerLevel;

  @Autowired private TelegramSender telegramSender;

  @Override
  public void debug(String message) {
    if (loggerLevel.getLevel().matchLevel(LoggerLevel.DEBUG)) {
      telegramSender.sendMessage(botToken, backupChat, "<pre>[DEBUG]</pre>\n" + message, true);
    }
  }

  @Override
  public void info(String message) {
    if (loggerLevel.getLevel().matchLevel(LoggerLevel.INFO)) {
      telegramSender.sendMessage(botToken, backupChat, "<pre>[INFO]</pre>\n" + message);
    }
  }

  @Override
  public void warn(String message) {
    if (loggerLevel.getLevel().matchLevel(LoggerLevel.WARN)) {
      telegramSender.sendMessage(botToken, backupChat, "[ #WARNING ]\n⚠️⚠️⚠️⚠️⚠️\n" + message);
    }
  }

  @Override
  public void error(String s, Exception e) {
    if (loggerLevel.getLevel().matchLevel(LoggerLevel.ERROR)) {
      telegramSender.sendMessage(
          botToken,
          backupChat,
          "[ #ERROR ]\n❗️❗️❗️❗️❗️\n"
              + s
              + "\n"
              + e.getMessage()
              + "\nStacktrace:\n"
              + getStacktrace(e));
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
}
