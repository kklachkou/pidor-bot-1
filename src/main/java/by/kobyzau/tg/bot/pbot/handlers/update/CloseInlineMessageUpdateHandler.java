package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.dto.CloseInlineMessageInlineDto;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageBotAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;
import java.util.Optional;

@Order
@Component
public class CloseInlineMessageUpdateHandler implements UpdateHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Value("${bot.username}")
  private String botUserName;

  private final Selection<Text> emoji =
      new ConsistentSelection<>("\uD83D\uDE42").map(SimpleText::new);

  @Override
  public boolean handleUpdate(Update update) {
    CallbackQuery callbackQuery = update.getCallbackQuery();
    if (callbackQuery == null) {
      return false;
    }
    Message prevMessage = callbackQuery.getMessage();
    Optional<CloseInlineMessageInlineDto> data =
        StringUtil.deserialize(callbackQuery.getData(), CloseInlineMessageInlineDto.class);
    if (prevMessage == null
        || prevMessage.getFrom() == null
        || !data.isPresent()
        || !Objects.equals(SerializableInlineType.CLOSE_INLINE.getIndex(), data.get().getIndex())
        || !botUserName.equalsIgnoreCase(prevMessage.getFrom().getUserName())) {
      return false;
    }
    botActionCollector.add(new EditMessageBotAction(prevMessage, emoji.next()));
    return true;
  }

}
