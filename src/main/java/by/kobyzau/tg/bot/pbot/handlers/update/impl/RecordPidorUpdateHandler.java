package by.kobyzau.tg.bot.pbot.handlers.update.impl;

import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandlerStage;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class RecordPidorUpdateHandler implements UpdateHandler {
  @Autowired private PidorRepository pidorRepository;

  @Override
  public boolean handleUpdate(Update update) {
    if (update.hasMessage()) {
      Message message = update.getMessage();
      Long chatId = message.getChatId();
      Long userId = message.getFrom().getId();
      Optional<Pidor> pidor = pidorRepository.getByChatAndPlayerTgId(chatId, userId);
      if (pidor.isPresent()) {
        pidor.get().setUsernameLastUpdated(DateUtil.now());
        pidorRepository.update(pidor.get());
      }
    }
    return false;
  }

  @Override
  public UpdateHandlerStage getStage() {
    return UpdateHandlerStage.TRACE_USER;
  }
}
