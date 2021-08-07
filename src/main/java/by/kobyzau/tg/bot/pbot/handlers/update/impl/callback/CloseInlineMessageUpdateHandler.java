package by.kobyzau.tg.bot.pbot.handlers.update.impl.callback;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.CallbackUpdateHandler;
import by.kobyzau.tg.bot.pbot.model.dto.CloseInlineMessageInlineDto;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageBotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType.CLOSE_INLINE;

@Component
public class CloseInlineMessageUpdateHandler
    extends CallbackUpdateHandler<CloseInlineMessageInlineDto> {

  @Autowired private BotActionCollector botActionCollector;

  private final Selection<Text> emoji =
      new ConsistentSelection<>("\uD83D\uDE42").map(SimpleText::new);

  @Override
  protected Class<CloseInlineMessageInlineDto> getDtoType() {
    return CloseInlineMessageInlineDto.class;
  }

  @Override
  protected SerializableInlineType getSerializableType() {
    return CLOSE_INLINE;
  }

  @Override
  protected void handleCallback(Update update, CloseInlineMessageInlineDto dto) {
    Message prevMessage = update.getCallbackQuery().getMessage();
    botActionCollector.add(new EditMessageBotAction(prevMessage, emoji.next()));
  }
}
