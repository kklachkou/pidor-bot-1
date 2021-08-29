package by.kobyzau.tg.bot.pbot.sender;


import by.kobyzau.tg.bot.pbot.sender.methods.SendMethod;

public interface BotSender {

  void send(long chatId, SendMethod<?> method);

  <T> void send(long chatId, SendMethod<T> method, BotApiMethodCallback<T> callback);
}
