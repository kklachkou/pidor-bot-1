package by.kobyzau.tg.bot.pbot.bots;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.Optional;

public interface Bot {

  String getBotUsername();

  String getBotToken();

  void botConnect();

  <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method)
      throws TelegramApiException;

  Message execute(SendSticker var1) throws TelegramApiException;

  Message execute(SendDocument doc) throws TelegramApiException;

  Message execute(SendPhoto sendPhoto) throws TelegramApiException;

  Message execute(SendAnimation animation) throws TelegramApiException;

  Optional<File> getFile(String fileId);
}
