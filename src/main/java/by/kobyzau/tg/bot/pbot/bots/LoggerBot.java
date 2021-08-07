package by.kobyzau.tg.bot.pbot.bots;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Component
public class LoggerBot extends DefaultAbsSender {

  @Value("${logger.tg.token}")
  private String botToken;

  public LoggerBot() {
    super(new DefaultBotOptions());
  }

  @Override
  public String getBotToken() {
    return botToken;
  }
}
