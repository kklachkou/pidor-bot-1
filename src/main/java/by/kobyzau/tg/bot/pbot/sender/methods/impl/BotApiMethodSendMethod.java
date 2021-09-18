package by.kobyzau.tg.bot.pbot.sender.methods.impl;

import by.kobyzau.tg.bot.pbot.sender.methods.SendMethod;
import lombok.EqualsAndHashCode;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

@EqualsAndHashCode
public class BotApiMethodSendMethod<T extends Serializable> implements SendMethod<T> {
  private final BotApiMethod<T> method;

  public BotApiMethodSendMethod(BotApiMethod<T> method) {
    this.method = method;
  }

  @Override
  public String getMethod() {
    return method.getMethod();
  }

  @Override
  public T send(AbsSender sender) throws TelegramApiException {
    return sender.execute(method);
  }

  @Override
  public String toString() {
    return "BotApiMethodSendMethod{" + "method=" + method.getClass().getSimpleName() + '}';
  }
}
