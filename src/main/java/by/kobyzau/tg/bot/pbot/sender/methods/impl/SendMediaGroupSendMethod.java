package by.kobyzau.tg.bot.pbot.sender.methods.impl;


import by.kobyzau.tg.bot.pbot.sender.methods.SendMethod;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@RequiredArgsConstructor
public class SendMediaGroupSendMethod implements SendMethod<List<Message>> {

  private final SendMediaGroup sendMediaGroup;

  @Override
  public List<Message> send(AbsSender sender) throws TelegramApiException {
    return sender.execute(sendMediaGroup);
  }

  @Override
  public String getMethod() {
    return "sendMediaGroup";
  }
}
