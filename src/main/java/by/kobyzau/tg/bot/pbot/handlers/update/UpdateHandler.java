package by.kobyzau.tg.bot.pbot.handlers.update;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {

  boolean handleUpdate(Update update);

  int DICE_ORDER = 0;
  int EXCLUDE_ORDER = 10;
  int ELECTION_ORDER = 15;
  int EDIT_TEXT_ORDER = 20;
  int COMMAND_ORDER = 1000;
}
