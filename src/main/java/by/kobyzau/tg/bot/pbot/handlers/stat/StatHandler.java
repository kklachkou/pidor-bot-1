package by.kobyzau.tg.bot.pbot.handlers.stat;

import by.kobyzau.tg.bot.pbot.model.StatType;

public interface StatHandler {

    void printStat(long chatId);

    StatType getType();
}
