package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.function.Consumer;

public class PollBotAction implements BotAction<Message> {

  private final SendPoll sendPoll;
  private final Consumer<Message> postAction;

  public PollBotAction(SendPoll sendPoll, Consumer<Message> postAction) {
    this.sendPoll = sendPoll;
    if (DateUtil.sleepTime()) {
      this.sendPoll.disableNotification();
    }
    this.postAction = postAction;
  }

  @Override
  public Message process(Bot bot) throws TelegramApiException {
    Message result = bot.execute(sendPoll);
    if (postAction != null) {
      postAction.accept(result);
    }
    return result;
  }

  @Override
  public long getChatId() {
    return StringUtil.parseLong(sendPoll.getChatId(), 0);
  }

  @Override
  public String toString() {
    return "PollBotAction{" + "sendPoll=" + sendPoll + ", postAction=" + postAction + '}';
  }
}
