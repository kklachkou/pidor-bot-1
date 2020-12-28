package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.program.Version;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.annotation.PostConstruct;

@Component
public class VersionCommandHandler implements CommandHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private TelegramService telegramService;

  @Value("${app.version}")
  private String version;

  private Text versionDescription;

  @PostConstruct
  private void init() {
    this.versionDescription =
        new TextBuilder(
                new ParametizedText(
                    "<pre><u>Новая версия бота - {0}</u></pre>", new SimpleText(version)))
            .append(new NewLineText())
            .append(new NewLineText())
            .append(Version.getLast().getDescription());
  }

  @Override
  public void processCommand(Message message, String text) {
    telegramService.getChatIds().forEach(this::sendVersion);
  }

  private void sendVersion(long chatId) {
    botActionCollector.text(chatId, versionDescription);
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.sticker(chatId, Version.getLast().getSticker().getRandom());
  }

  @Override
  public Command getCommand() {
    return Command.VERSION;
  }
}
