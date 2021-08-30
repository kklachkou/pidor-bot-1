package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class CallbackBotAction<T> implements BotAction<T> {
    private final BotAction<T> botAction;
    private final Consumer<T> callback;

    @Override
    public T process(Bot bot) throws TelegramApiException {
        T result = botAction.process(bot);
        callback.accept(result);
        return result;
    }

    @Override
    public long getChatId() {
        return botAction.getChatId();
    }

    @Override
    public boolean hasLimit() {
        return botAction.hasLimit();
    }

    @Override
    public String toString() {
        return botAction.toString();
    }
}
