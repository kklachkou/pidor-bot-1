package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.stat.StatHandlerFactory;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.model.dto.StatInlineDto;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.tg.action.AnswerCallbackBotAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StatUpdateHandler implements UpdateHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Value("${bot.username}")
  private String botUserName;

  @Autowired private StatHandlerFactory statHandlerFactory;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @Override
  public boolean handleUpdate(Update update) {
    CallbackQuery callbackQuery = update.getCallbackQuery();
    if (callbackQuery == null) {
      return false;
    }
    Message prevMessage = callbackQuery.getMessage();
    Optional<StatInlineDto> data =
        StringUtil.deserialize(callbackQuery.getData(), StatInlineDto.class);
    if (prevMessage == null
        || prevMessage.getFrom() == null
        || !data.isPresent()
        || data.get().getType() == null
        || !Objects.equals(SerializableInlineType.STAT.getIndex(), data.get().getIndex())
        || !botUserName.equalsIgnoreCase(prevMessage.getFrom().getUserName())) {
      return false;
    }
    executor.execute(() -> handle(callbackQuery.getId(), prevMessage, data.get()));
    return true;
  }

  private void handle(String callbackId, Message prevMessage, StatInlineDto data) {
    long chatId = prevMessage.getChatId();
    StatType statType = data.getType();
    statHandlerFactory.getHandler(statType).printStat(prevMessage.getChatId());
    botActionCollector.add(
        new AnswerCallbackBotAction(chatId, callbackId, new SimpleText("Статистика получена")));
  }

}
