package by.kobyzau.tg.bot.pbot.sender;

public interface BotApiMethodCallback<T> {

  void handleResult(T result);
}
