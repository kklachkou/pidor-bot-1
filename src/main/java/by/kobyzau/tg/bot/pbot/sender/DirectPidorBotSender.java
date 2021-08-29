package by.kobyzau.tg.bot.pbot.sender;

import by.kobyzau.tg.bot.pbot.bots.PidorBot;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.sender.methods.SendMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("directPidorBotSender")
@RequiredArgsConstructor
public class DirectPidorBotSender implements BotSender {

  private final PidorBot bot;
  private final Logger logger;

  @Override
  public void send(long chatId, SendMethod<?> method) {
    try {
      method.send(bot);
    } catch (Exception e) {
      logger.error("Cannot send to chat " + chatId + ": " + e.getMessage(), e);
    }
  }

  @Override
  public <T> void send(long chatId, SendMethod<T> method, BotApiMethodCallback<T> callback) {
    try {
      T result = method.send(bot);
      if (callback != null) {
        callback.handleResult(result);
      }
    } catch (Exception e) {
      logger.error("Cannot send to chat " + chatId + ": " + e.getMessage(), e);
    }
  }
}
