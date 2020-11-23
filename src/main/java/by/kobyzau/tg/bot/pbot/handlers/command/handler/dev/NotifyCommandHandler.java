package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.TelegramSender;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class NotifyCommandHandler implements CommandHandler {

  @Autowired private TelegramService telegramService;

  @Autowired private TelegramSender telegramSender;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private Logger logger;

  @Autowired private Bot bot;

  @Override
  public void processCommand(Message message, String text) {
    List<Long> chatIds = telegramService.getChatIds();
    if (CollectionUtil.isEmpty(chatIds)) {
      botActionCollector.collectHTMLMessage(message.getChatId(), "No Active Chats");
      return;
    }
    if (StringUtil.isBlank(text)) {
      botActionCollector.collectHTMLMessage(message.getChatId(), "No message");
      return;
    }

    for (Long chatId : chatIds) {
      try {
        telegramSender.sendMessage(bot.getBotToken(), String.valueOf(chatId), text);
      } catch (Exception ex) {
        logger.error("Cannot send to chat " + chatId, ex);
      }
    }
  }

  @Override
  public Command getCommand() {
    return Command.NOTIFY;
  }
}
