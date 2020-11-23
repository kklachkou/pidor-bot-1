package by.kobyzau.tg.bot.pbot.handlers.command.handler.schedule;

import by.kobyzau.tg.bot.pbot.program.text.Text;

import java.time.LocalDate;
import java.util.Optional;

public interface ScheduleDayTextItem {

  Optional<Text> getTextItem(LocalDate localDate);
}
