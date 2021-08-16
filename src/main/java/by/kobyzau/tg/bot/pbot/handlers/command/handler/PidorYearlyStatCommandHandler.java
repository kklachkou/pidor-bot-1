package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.PidorYearlyStatService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class PidorYearlyStatCommandHandler implements CommandHandler {
  @Autowired private PidorYearlyStatService pidorYearlyStatService;
  @Autowired private TelegramService telegramService;
  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    int year = StringUtil.parseInt(text, DateUtil.now().getYear() - 1);
    for (Long chatId : telegramService.getChatIds()) {
      pidorYearlyStatService.createYearlyStat(chatId, year);
    }
    botActionCollector.text(message.getChatId(), new SimpleText("Данные сохранены"));
  }

  @Override
  public Command getCommand() {
    return Command.YEARLY_STAT;
  }
}
