package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.PidorStatus;

public interface PidorStatusService {

  PidorStatus getPidorStatus(long chatId, int year);
}
