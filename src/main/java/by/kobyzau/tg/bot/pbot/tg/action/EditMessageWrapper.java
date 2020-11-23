package by.kobyzau.tg.bot.pbot.tg.action;

import by.kobyzau.tg.bot.pbot.bots.Bot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.concurrent.Executor;

public class EditMessageWrapper implements BotAction<Boolean> {

  private final BotAction<Message> botAction;
  private final BotActionCollector botActionCollector;
  private final Executor executor;
  private final List<EditMessageChain> editMessageChains;

  public EditMessageWrapper(
      BotAction<Message> botAction,
      BotActionCollector botActionCollector,
      Executor executor,
      List<EditMessageChain> editMessageChains) {
    this.botAction = botAction;
    this.botActionCollector = botActionCollector;
    this.executor = executor;
    this.editMessageChains = editMessageChains;
  }

  @Override
  public Boolean process(Bot bot) throws TelegramApiException {
    Message message = botAction.process(bot);
    if (executor != null) {
      executor.execute(() -> editMessage(bot, message));
    } else {
      editMessage(bot, message);
    }
    return true;
  }

  private void editMessage(Bot bot, Message message) {
    for (EditMessageChain editMessageChain : editMessageChains) {
      if (editMessageChain.delay > 0) {
        try {
          Thread.sleep((int) editMessageChain.delay * 1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      EditMessageBotAction editMessageBotAction =
          new EditMessageBotAction(message, editMessageChain.newText);
      if (botActionCollector != null) {
        botActionCollector.add(editMessageBotAction);
      } else {
        try {
          editMessageBotAction.process(bot);
        } catch (TelegramApiException e) {
          e.printStackTrace();
          throw new RuntimeException("Cannot edit message", e);
        }
      }
    }
  }

  @Override
  public long getChatId() {
    return botAction.getChatId();
  }

  public static class EditMessageChain {
    private final Text newText;
    private final double delay;

    public EditMessageChain(Text newText, double delay) {
      this.newText = newText;
      this.delay = delay;
    }
  }
}
