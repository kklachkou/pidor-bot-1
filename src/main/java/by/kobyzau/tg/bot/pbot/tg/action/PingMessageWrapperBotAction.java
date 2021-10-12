package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class PingMessageWrapperBotAction implements BotAction<Message> {

  private final BotAction<Message> botAction;

  @Override
  public Message process(Bot bot) throws TelegramApiException {

    Message message = botAction.process(bot);
    User botUser = bot.execute(GetMe.builder().build());
    ChatMember botMember =
        bot.execute(
            GetChatMember.builder()
                .chatId(String.valueOf(botAction.getChatId()))
                .userId(botUser.getId())
                .build());
    if (TGUtil.canPinMessage(botMember)) {
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
  public boolean hasLimit() {
    return true;
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
