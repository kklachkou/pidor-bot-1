package by.kobyzau.tg.bot.pbot.handlers.update;

import org.springframework.core.Ordered;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.function.Predicate;

public interface UpdateHandler extends Predicate<LocalDate> {

  boolean handleUpdate(Update update);

  int DICE_ORDER = 0;
  int ASSASSIN_ORDER = 5;
  int EXCLUDE_ORDER = 10;
  int POLL_ORDER = 15;
  int EDIT_TEXT_ORDER = 20;
  int COMMAND_ORDER = Ordered.LOWEST_PRECEDENCE;
}
