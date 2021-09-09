package by.kobyzau.tg.bot.pbot.config.health;

import by.kobyzau.tg.bot.pbot.bots.PidorBot;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
@RequiredArgsConstructor
public class TelegramBotHealthIndicator extends AbstractHealthIndicator {

  private final PidorBot pidorBot;

  @Override
  protected void doHealthCheck(Health.Builder builder) throws Exception {
    User user = pidorBot.getMe();
    if (user != null) {
      builder.up();
    } else {
      builder.outOfService().withDetail("message", "Pidor Bot User is not found");
    }
  }
}
