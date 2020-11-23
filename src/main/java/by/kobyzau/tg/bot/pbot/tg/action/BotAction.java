package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface BotAction<T> {

    T process(Bot bot) throws TelegramApiException;

    long getChatId();
}
