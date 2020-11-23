package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class TimeCommandHandler implements CommandHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    botActionCollector.collectHTMLMessage(message.getChatId(), "Now : " + DateUtil.now());
    botActionCollector.collectHTMLMessage(
        message.getChatId(), "Now time : " + DateUtil.currentTime());
  }

  @Override
  public Command getCommand() {
    return Command.CURRENT_TIME;
  }
}
