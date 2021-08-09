package by.kobyzau.tg.bot.pbot.program.logger;

import by.kobyzau.tg.bot.pbot.bots.LoggerBot;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component("TGLogger")
public class TelegramLogger implements Logger {

  @Value("${logger.tg.chat}")
  private String backupChat;

  @Autowired private LoggerBot loggerBot;

  @Autowired
  @Qualifier("loggerExecutor")
  private Executor executor;

  @Override
  public void debug(String message) {
    executor.execute(() -> sendMessage("<pre>[DEBUG]</pre>\n" + message, 0));
  }

  @Override
  public void info(String message) {
    executor.execute(() -> sendMessage("<pre>[INFO]</pre>\n" + message, 0));
  }

  @Override
  public void warn(String message) {
    executor.execute(() -> sendMessage("[ #WARNING ]\n⚠️⚠️⚠️⚠️⚠️\n" + message, 0));
  }

  @Override
  public void error(String s, Exception e) {
    executor.execute(
        () ->
            sendMessage(
                "[ #ERROR ]\n❗️❗️❗️❗️❗️\n"
                    + s
                    + "\n"
                    + e.getMessage()
                    + "\nStacktrace:\n<pre>"
                    + getStacktrace(e)
                    + "</pre>",
                0));
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
              .append(stackTraceElement.getClassName().replace("by.kobyzau.tg.bot.pbot", ""))
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

  private void sendMessage(String message, int attempt) {
    try {
      loggerBot.execute(
          SendMessage.builder()
              .parseMode("html")
              .text(message)
              .chatId(backupChat)
              .disableNotification(true)
              .build());
    } catch (Exception e) {
      e.printStackTrace();
      if (attempt < 10) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        sendMessage(message, attempt + 1);
      }
    }
  }
}
