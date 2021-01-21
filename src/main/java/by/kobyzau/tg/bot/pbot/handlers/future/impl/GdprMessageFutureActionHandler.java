package by.kobyzau.tg.bot.pbot.handlers.future.impl;

import by.kobyzau.tg.bot.pbot.handlers.future.FutureActionHandler;
import by.kobyzau.tg.bot.pbot.model.dto.GdprMessageDto;
import by.kobyzau.tg.bot.pbot.service.FutureActionService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GdprMessageFutureActionHandler implements FutureActionHandler {

  @Autowired private TelegramService telegramService;

  @Override
  public FutureActionService.FutureActionType getType() {
    return FutureActionService.FutureActionType.GDPR_MESSAGE;
  }

  @Override
  public void processAction(String data) {
    Optional<GdprMessageDto> messageDto = StringUtil.deserialize(data, GdprMessageDto.class);
    messageDto.ifPresent(m -> telegramService.deleteMessage(m.getChatId(), m.getMessageId()));
  }
}
