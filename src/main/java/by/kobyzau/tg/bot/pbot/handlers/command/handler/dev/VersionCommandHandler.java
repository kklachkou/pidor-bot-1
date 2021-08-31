package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.model.FeedbackType;
import by.kobyzau.tg.bot.pbot.model.dto.AppVersionDto;
import by.kobyzau.tg.bot.pbot.program.Version;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.service.FeedbackService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.service.github.GithubService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.ReplyKeyboardBotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import javax.annotation.PostConstruct;

@Component
public class VersionCommandHandler implements CommandHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private TelegramService telegramService;
  @Autowired private FeedbackService feedbackService;
  @Autowired private GithubService githubService;

  private Text versionDescription;

  @PostConstruct
  private void init() {
    AppVersionDto appVersion = githubService.getAppVersion();
    this.versionDescription =
        new TextBuilder(
                new ParametizedText(
                    "<pre><u>Новая версия бота - {0}</u></pre>",
                    new SimpleText(appVersion.getNumber())))
            .append(new NewLineText())
            .append(new NewLineText())
            .append(Version.getLast().getDescription());
  }

  @Override
  public void processCommand(Message message, String text) {
    feedbackService.removeAllFeedbacks(FeedbackType.VERSION);
    telegramService.getChatIds().forEach(this::sendVersion);
  }

  private void sendVersion(long chatId) {

    botActionCollector.add(
        new ReplyKeyboardBotAction(
            chatId,
            versionDescription,
            InlineKeyboardMarkup.builder()
                .keyboardRow(feedbackService.getButtons(FeedbackType.VERSION))
                .build(),
            null));
    botActionCollector.wait(chatId, ChatAction.TYPING);
    botActionCollector.sticker(chatId, Version.getLast().getSticker().getRandom());
  }

  @Override
  public Command getCommand() {
    return Command.VERSION;
  }
}
