package by.kobyzau.tg.bot.pbot.sender.methods.impl;


import by.kobyzau.tg.bot.pbot.sender.methods.SendMethod;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@EqualsAndHashCode
@RequiredArgsConstructor
public class SendMessageSendMethod implements SendMethod<Message> {

  private final long chatId;
  private final String text;

  @Override
  public Message send(AbsSender sender) throws TelegramApiException {
    return sender.execute(
        SendMessage.builder().chatId(String.valueOf(chatId)).text(text).parseMode("html").build());
  }

  @Override
  public String getMethod() {
    return "sendmessage";
  }
}
