package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class NotifyCommandHandler implements CommandHandler {

  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private Logger logger;

  @Override
  public void processCommand(Message message, String text) {
    List<Long> chatIds = telegramService.getChatIds();
    if (CollectionUtil.isEmpty(chatIds)) {
      botActionCollector.text(message.getChatId(), new SimpleText("No Active Chats"));
      return;
    }
    if (StringUtil.isBlank(text)) {
      botActionCollector.text(message.getChatId(), new SimpleText("No message"));
      return;
    }

    for (Long chatId : chatIds) {
      botActionCollector.text(chatId, new SimpleText(text));
    }
  }

  @Override
  public Command getCommand() {
    return Command.NOTIFY;
  }
}
