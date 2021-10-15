package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class AnswerCallbackBotAction implements BotAction<Boolean> {

  private final long chatId;
  private final String callbackId;
  private final Text text;
  private final boolean asAlert;

  public AnswerCallbackBotAction(long chatId, String callbackId, Text text) {
    this(chatId, callbackId, text, false);
  }

  public AnswerCallbackBotAction(long chatId, String callbackId, Text text, boolean asAlert) {
    this.chatId = chatId;
    this.callbackId = callbackId;
    this.text = text;
    this.asAlert = asAlert;
  }

  @Override
  public Boolean process(Bot bot) throws TelegramApiException {
    return bot.execute(
        AnswerCallbackQuery.builder()
            .callbackQueryId(callbackId)
            .text(text.text())
            .showAlert(asAlert)
            .build());
  }

  @Override
  public long getChatId() {
    return chatId;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + ": " + chatId + "-" + callbackId + " - " + text;
  }

  @Override
  public boolean hasLimit() {
    return false;
  }
}
