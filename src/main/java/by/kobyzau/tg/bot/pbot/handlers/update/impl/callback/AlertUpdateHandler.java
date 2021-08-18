package by.kobyzau.tg.bot.pbot.handlers.update.impl.callback;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.handlers.update.CallbackUpdateHandler;
import by.kobyzau.tg.bot.pbot.model.dto.AlertDto;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class AlertUpdateHandler extends CallbackUpdateHandler<AlertDto> {
  @Autowired private Bot bot;
  @Autowired private Logger logger;

  @Override
  protected Class<AlertDto> getDtoType() {
    return AlertDto.class;
  }

  @Override
  protected SerializableInlineType getSerializableType() {
    return SerializableInlineType.ALERT;
  }

  @Override
  protected void handleCallback(Update update, AlertDto dto) {
    String message = dto.getMessage();
    boolean alert = dto.getAlert();
    Integer cache = dto.getCache();
    try {
      bot.execute(
          AnswerCallbackQuery.builder()
              .text(message)
              .showAlert(alert)
              .cacheTime(cache)
              .callbackQueryId(update.getCallbackQuery().getId())
              .build());
    } catch (Exception e) {
      logger.error("Cannot answer callback message " + message, e);
    }
  }
}
