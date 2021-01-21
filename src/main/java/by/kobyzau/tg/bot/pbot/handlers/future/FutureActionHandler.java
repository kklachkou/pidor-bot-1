package by.kobyzau.tg.bot.pbot.handlers.future;

import by.kobyzau.tg.bot.pbot.service.FutureActionService;

public interface FutureActionHandler {

  FutureActionService.FutureActionType getType();

  void processAction(String data);
}
