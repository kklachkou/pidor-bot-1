package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class TestCommandHandler implements CommandHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private TelegramService telegramService;

  @Override
  public void processCommand(Message message, String text) {
    botActionCollector.text(
            message.getChatId(),
            telegramService
                    .getChat(-1001363724461L)
                    .map(c -> "chat")
                    .map(SimpleText::new)
                    .orElse(new SimpleText("no chat")));
    botActionCollector.text(
            message.getChatId(),
            telegramService
                    .getChat(-1001363724461L)
                    .map(Chat::getPinnedMessage)
                    .map(c -> "pinned")
                    .map(SimpleText::new)
                    .orElse(new SimpleText("no pinned")));
    telegramService
        .getChat(-1001363724461L)
        .map(Chat::getPinnedMessage)
        .map(this::getUsersFromMessage)
        .orElseGet(Collections::emptyList)
        .stream()
        .map(User::getFirstName)
        .forEach(name -> botActionCollector.text(message.getChatId(), new SimpleText(name)));
  }

  private List<User> getUsersFromMessage(Message message) {
    List<User> users = new ArrayList<>();
    users.add(message.getFrom());
    users.add(message.getForwardFrom());
    if (message.getReplyToMessage() != null) {
      users.addAll(getUsersFromMessage(message.getReplyToMessage()));
    }
    if (message.getEntities() != null) {
      message.getEntities().stream()
          .map(MessageEntity::getUser)
          .filter(Objects::nonNull)
          .forEach(users::add);
    }
    if (message.getCaptionEntities() != null) {
      message.getCaptionEntities().stream()
          .map(MessageEntity::getUser)
          .filter(Objects::nonNull)
          .forEach(users::add);
    }
    if (message.getPinnedMessage() != null) {
      users.addAll(getUsersFromMessage(message.getPinnedMessage()));
    }
    users.addAll(message.getNewChatMembers());
    return users;
  }

  @Override
  public Command getCommand() {
    return Command.TEST;
  }
}
