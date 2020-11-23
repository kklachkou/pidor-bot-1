package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.PrioritySelection;

public interface PidorFunnyAction {

  void processFunnyAction(long chatId, Pidor pidorOfTheDay);


  PrioritySelection.Priority getPriority();
}
