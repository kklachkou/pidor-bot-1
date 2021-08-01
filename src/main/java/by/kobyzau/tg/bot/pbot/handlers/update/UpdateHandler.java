package by.kobyzau.tg.bot.pbot.handlers.update;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {

  boolean handleUpdate(Update update);

  UpdateHandlerStage getStage();
}
