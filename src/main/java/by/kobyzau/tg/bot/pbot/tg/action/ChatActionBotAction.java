package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ChatActionBotAction implements BotAction<Boolean> {

  private final long chatId;
  private final ChatAction chatAction;

  public ChatActionBotAction(long chatId, ChatAction chatAction) {
    this.chatId = chatId;
    this.chatAction = chatAction;
  }

  @Override
  public Boolean process(Bot bot) throws TelegramApiException {
    return bot.execute(
        SendChatAction.builder()
            .chatId(String.valueOf(chatId))
            .action(chatAction.getAction())
            .build());
  }

  @Override
  public long getChatId() {
    return chatId;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + ": " + chatId + "-" + chatAction;
  }

  @Override
  public boolean hasLimit() {
    return false;
  }
}
