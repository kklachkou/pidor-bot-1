package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor.repeat;

import by.kobyzau.tg.bot.pbot.model.Pidor;

public interface RepeatFunnyAction {

  void processFunnyAction(long chatId, int numWins, Pidor pidorOfTheDay);
}
