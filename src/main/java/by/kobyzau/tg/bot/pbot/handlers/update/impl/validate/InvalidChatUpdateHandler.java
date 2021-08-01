package by.kobyzau.tg.bot.pbot.handlers.update.impl.validate;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandlerStage;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class InvalidChatUpdateHandler implements UpdateHandler {

  @Autowired private Logger logger;
  @Autowired private BotService botService;
  @Autowired private BotActionCollector botActionCollector;

  @Override
  public UpdateHandlerStage getStage() {
    return UpdateHandlerStage.VALIDATE;
  }

  @Override
  public boolean handleUpdate(Update update) {
    Optional<Chat> chat = getChat(update);
    if (chat.isPresent()) {
      if (!botService.isChatValid(chat.get())) {
        botActionCollector.text(
            update.getMessage().getChatId(),
            new SimpleText(
                "Бот работает только в групповых чатах\n\nДобавляйте бота в любой чат и веселитесь!\nУбедитесь, что у бота есть права на отправку сообщений"));
        return true;
      }
    } else {
      logger.debug("Update does not contain chat: " + update);
      return true;
    }

    return false;
  }

  private Optional<Chat> getChat(Update update) {
    if (update.hasMessage()) {
      return Optional.of(update.getMessage()).map(Message::getChat);
    }
    if (update.hasCallbackQuery()) {
      return Optional.of(update.getCallbackQuery())
          .map(CallbackQuery::getMessage)
          .map(Message::getChat);
    }
    return Optional.empty();
  }
}
