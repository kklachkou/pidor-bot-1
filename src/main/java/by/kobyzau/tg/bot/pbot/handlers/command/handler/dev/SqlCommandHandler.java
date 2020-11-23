package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class SqlCommandHandler implements CommandHandler {

  @Autowired private DataSource dataSource;
  @Autowired private Logger logger;
  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    if (StringUtil.isBlank(text)) {
      return;
    }
    botActionCollector.text(
        chatId,
        new ParametizedText(
            "Executing the following query:\n\n<pre>{0}</pre>", new SimpleText(text)));
    try (Connection c = dataSource.getConnection();
        Statement s = c.createStatement()) {
      if (s.execute(text)) {
        botActionCollector.text(chatId, new SimpleText("Done"));
      } else {
        botActionCollector.text(chatId, new SimpleText("Done with empty result"));
      }
    } catch (Exception e) {
      logger.error("Cannot execute sql: " + TGUtil.escapeHTML(text), e);
    }
  }

  @Override
  public Command getCommand() {
    return Command.SQL;
  }
}
