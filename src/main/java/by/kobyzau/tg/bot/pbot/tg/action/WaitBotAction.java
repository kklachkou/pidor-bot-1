package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class WaitBotAction implements BotAction<Boolean> {

  private final long chatId;
  private final int seconds;
  private final ChatAction waitChatAction;

  public WaitBotAction(long chatId, int seconds, ChatAction waitChatAction) {
    this.chatId = chatId;
    this.seconds = seconds;
    this.waitChatAction = waitChatAction;
  }

  @Override
  public Boolean process(Bot bot) throws TelegramApiException {
    if (waitChatAction != null) {
      bot.execute(
          SendChatAction.builder()
              .chatId(String.valueOf(chatId))
              .action(waitChatAction.getAction())
              .build());
    }
    try {
      Thread.sleep(seconds * 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException("Cannot sleep in wait action", e);
    }
    return true;
  }

  @Override
  public long getChatId() {
    return chatId;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + ": " + seconds + "-" + waitChatAction;
  }

  @Override
  public boolean hasLimit() {
    return false;
  }
}
