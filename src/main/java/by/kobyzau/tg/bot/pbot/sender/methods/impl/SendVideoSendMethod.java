package by.kobyzau.tg.bot.pbot.sender.methods.impl;

import by.kobyzau.tg.bot.pbot.sender.methods.SendMethod;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class SendVideoSendMethod implements SendMethod<Message> {
  private final SendVideo sendVideo;

  @Override
  public Message send(AbsSender sender) throws TelegramApiException {
    return sender.execute(sendVideo);
  }

  @Override
  public String getMethod() {
    return "sendVideo";
  }
}
