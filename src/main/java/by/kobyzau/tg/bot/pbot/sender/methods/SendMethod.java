package by.kobyzau.tg.bot.pbot.sender.methods;

import by.kobyzau.tg.bot.pbot.sender.methods.impl.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.List;

public interface SendMethod<T> {

  T send(AbsSender sender) throws TelegramApiException;

  String getMethod();

  static <T extends Serializable> SendMethod<T> method(BotApiMethod<T> method) {
    return new BotApiMethodSendMethod<>(method);
  }

  static SendMethod<Message> sendMessage(long chatId, String text) {
    return new SendMessageSendMethod(chatId, text);
  }

  static SendMethod<Message> method(SendSticker method) {
    return new SendStickerSendMethod(method);
  }

  static SendMethod<Message> method(SendPhoto method) {
    return new SendPhotoSendMethod(method);
  }

  static SendMethod<List<Message>> method(SendMediaGroup method) {
    return new SendMediaGroupSendMethod(method);
  }

  static SendMethod<Message> method(SendVideo method) {
    return new SendVideoSendMethod(method);
  }
}
