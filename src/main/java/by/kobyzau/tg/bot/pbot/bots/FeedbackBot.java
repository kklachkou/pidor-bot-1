package by.kobyzau.tg.bot.pbot.bots;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class FeedbackBot extends TelegramLongPollingBot {

  @Autowired private Logger logger;
  @Autowired private BotActionCollector botActionCollector;

  @Value("${app.time.bot.reconnect}")
  private int reconnectTime;

  @Value("${bot.feedback.username}")
  private String botUserName;

  @Value("${bot.pidor.username}")
  private String pidorBotName;

  @Value("${bot.feedback.token}")
  private String botToken;

  @Value("${app.admin.userId}")
  private long adminUserId;

  @Override
  public void onUpdateReceived(Update update) {
    try {
      boolean feedbackHandled = false;
      if (update.hasMessage()) {
        Message message = update.getMessage();
        if (message.hasText() && message.getChat().isUserChat()) {
          feedbackHandled = true;
          String text = message.getText();
          User user = message.getFrom();
          if ("/start".equals(text)) {
            sendApiMethod(
                SendMessage.builder()
                    .text(
                        "Приветствую! Пришли мне предложения либо отзывы по боту @" + pidorBotName)
                    .parseMode("html")
                    .chatId(String.valueOf(user.getId()))
                    .build());
          } else {
            sendApiMethod(
                SendMessage.builder()
                    .text("Спасибо, фидбек получен")
                    .parseMode("html")
                    .chatId(String.valueOf(user.getId()))
                    .build());
            logger.info(
                new TextBuilder()
                    .append(new SimpleText("#Feedback"))
                    .append(new NewLineText())
                    .append(
                        new ParametizedText(
                            "{0}: {1}",
                            new SimpleText(TGUtil.escapeHTML(user.getFirstName())),
                            new LongText(user.getId())))
                    .append(new NewLineText())
                    .append(new NewLineText())
                    .append(new SimpleText(TGUtil.escapeHTML(text)))
                    .text());
          }
        }
      }
      if (!feedbackHandled) {
        sendApiMethod(
            SendMessage.builder()
                .text("Пишите мне только текст")
                .parseMode("html")
                .chatId(String.valueOf(adminUserId))
                .build());
      }
    } catch (Exception e) {
      logger.error("Cannot handle feedback", e);
    }
  }

  public void botConnect() {

    try {
      TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
      telegramBotsApi.registerBot(this);
    } catch (TelegramApiException e) {
      logger.error("Cant Connect. Pause " + reconnectTime / 1000 + "sec and try again", e);
      try {
        Thread.sleep(reconnectTime);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
        return;
      }
      botConnect();
    }
  }

  @Override
  public String getBotUsername() {
    return botUserName;
  }

  @Override
  public String getBotToken() {
    return botToken;
  }
}
