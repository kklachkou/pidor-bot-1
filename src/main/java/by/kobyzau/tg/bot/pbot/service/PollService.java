package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.CalendarSchedule;
import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PollService {

  @Autowired private CalendarSchedule calendarSchedule;

  public boolean isDateToPoll(LocalDate localDate) {
    return calendarSchedule.getItem(localDate) == ScheduledItem.POLL;
  }
}
