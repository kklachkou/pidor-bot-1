package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineObject;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.util.Optional;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class CallbackUpdateHandler<T extends SerializableInlineObject>
    implements UpdateHandler {

  protected abstract Class<T> getDtoType();

  protected abstract SerializableInlineType getSerializableType();

  protected abstract void handleCallback(Update update, T dto);

  @Override
  public boolean handleUpdate(Update update) {
    CallbackQuery callbackQuery = update.getCallbackQuery();
    if (callbackQuery == null
        || callbackQuery.getMessage() == null
        || StringUtil.isBlank(callbackQuery.getId())) {
      return false;
    }
    String data = callbackQuery.getData();
    Optional<T> dto = StringUtil.deserialize(data, getDtoType());
    if (!dto.isPresent() || dto.get().getIndex() != getSerializableType().getIndex()) {
      return false;
    }
    handleCallback(update, dto.get());
    return true;
  }

  @Override
  public UpdateHandlerStage getStage() {
    return UpdateHandlerStage.CALLBACK;
  }
}
