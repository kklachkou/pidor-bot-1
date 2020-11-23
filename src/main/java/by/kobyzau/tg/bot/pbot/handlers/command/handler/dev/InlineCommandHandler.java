package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.Collections;

@Component
@Profile("dev")
public class InlineCommandHandler implements CommandHandler {

  @Autowired private Bot bot;

  @Override
  public void processCommand(Message message, String text) {
    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    InlineKeyboardButton b1 = new InlineKeyboardButton("Перезапустить бота");
    b1.setCallbackData("b1");
    InlineKeyboardButton b2 = new InlineKeyboardButton("Присвоить очко");
    b2.setCallbackData("b2");
    inlineKeyboardMarkup.setKeyboard(Collections.singletonList(Arrays.asList(b1, b2)));
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(String.valueOf(message.getChatId()));
    sendMessage.setText("Чувак сбежал сбежал. Давать очко или переигрываем?\"");
    sendMessage.setReplyMarkup(inlineKeyboardMarkup);
    try {
      bot.execute(sendMessage);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Command getCommand() {
    return Command.INLINE;
  }
}
