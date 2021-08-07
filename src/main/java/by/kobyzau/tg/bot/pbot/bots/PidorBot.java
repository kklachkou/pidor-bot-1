package by.kobyzau.tg.bot.pbot.bots;

import by.kobyzau.tg.bot.pbot.collectors.ReceiveUpdateCollector;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.printer.UpdatePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Optional;

@Component
public class PidorBot extends TelegramLongPollingBot implements Bot {

  @Autowired private Logger logger;

  @Value("${app.time.bot.reconnect}")
  private int reconnectTime;

  @Value("${bot.pidor.username}")
  private String botUserName;

  @Value("${bot.pidor.token}")
  private String botToken;

  @Autowired private ReceiveUpdateCollector updateCollector;

  @Override
  public String getBotToken() {
    return botToken;
  }

  @Override
  public void onUpdateReceived(Update update) {
    logger.debug(
        "\uD83D\uDD04 New update received:\n\n<pre>" + new UpdatePrinter(update) + "</pre>");
    updateCollector.collectUpdate(update);
  }

  @Override
  public Optional<File> getFile(String fileId) {
    GetFile getFileMethod = new GetFile();
    getFileMethod.setFileId(fileId);
    try {
      return Optional.ofNullable(execute(getFileMethod));
    } catch (Exception e) {
      logger.error("Cannot get file " + fileId, e);
    }
    return Optional.empty();
  }

  @Override
  public String getBotUsername() {
    return botUserName;
  }

  @Override
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
}
