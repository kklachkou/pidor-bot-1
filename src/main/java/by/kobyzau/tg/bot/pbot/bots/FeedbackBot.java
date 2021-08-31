package by.kobyzau.tg.bot.pbot.bots;

import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Collections;

@Profile("prod")
@Component
public class FeedbackBot extends TelegramLongPollingBot {

  @Autowired private Logger logger;

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
      if (update.hasMessage()) {
        Message message = update.getMessage();
        if (message.hasText() && message.getChat().isUserChat()) {
          String text = message.getText();
          User user = message.getFrom();
          long chatId = user.getId();
          if ("/start".equals(text)) {
            sendApiMethod(
                SendMessage.builder()
                    .text(
                        "Приветствую! Пришли мне предложения либо отзывы по боту @" + pidorBotName)
                    .parseMode("html")
                    .chatId(String.valueOf(chatId))
                    .build());
          } else {
            sendApiMethod(
                CopyMessage.builder()
                    .fromChatId(String.valueOf(chatId))
                    .messageId(message.getMessageId())
                    .parseMode("html")
                    .chatId(String.valueOf(adminUserId))
                    .replyMarkup(
                        InlineKeyboardMarkup.builder()
                            .keyboardRow(
                                Collections.singletonList(
                                    InlineKeyboardButton.builder()
                                        .text(String.valueOf(chatId))
                                        .url("tg://user?id=" + chatId)
                                        .build()))
                            .build())
                    .build());
            logger.info(
                new TextBuilder()
                    .append(new SimpleText("#Feedback"))
                    .append(new NewLineText())
                    .append(
                        new ParametizedText(
                            "{0}: {1}",
                            new SimpleText(TGUtil.escapeHTML(user.getFirstName())),
                            new LongText(chatId)))
                    .append(new NewLineText())
                    .append(new NewLineText())
                    .append(new SimpleText(TGUtil.escapeHTML(text)))
                    .text());
            execute(
                SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text("Спасибо, отзыв получен")
                    .replyToMessageId(message.getMessageId())
                    .build());
          }
        }
      }
    } catch (Exception e) {
      logger.error("Cannot handle feedback: " + e.getMessage(), e);
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
