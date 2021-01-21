package by.kobyzau.tg.bot.pbot.checker;

import by.kobyzau.tg.bot.pbot.tg.action.BotAction;

public interface BotActionChecker {

    void check(BotAction<?> botAction);
}
