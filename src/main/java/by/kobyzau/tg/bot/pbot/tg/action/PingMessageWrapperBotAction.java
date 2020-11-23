package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class PingMessageWrapperBotAction implements BotAction<Message> {

  private final BotAction<Message> botAction;
  private final boolean pinMessage;

  public PingMessageWrapperBotAction(BotAction<Message> botAction) {
    this(botAction, true);
  }

  public PingMessageWrapperBotAction(BotAction<Message> botAction, boolean pinMessage) {
    this.botAction = botAction;
    this.pinMessage = pinMessage;
  }

  @Override
  public Message process(Bot bot) throws TelegramApiException {
    Message message = botAction.process(bot);
    if (pinMessage) {
      PinChatMessage pinChatMessage =
          PinChatMessage.builder()
              .chatId(String.valueOf(message.getChatId()))
              .messageId(message.getMessageId())
              .disableNotification(DateUtil.sleepTime())
              .build();
      bot.execute(pinChatMessage);
    }
    return message;
  }

  @Override
  public long getChatId() {
    return botAction.getChatId();
  }

  @Override
  public String toString() {
    return "PingMessageWrapperBotAction{" + "botAction=" + botAction + '}';
  }
}
