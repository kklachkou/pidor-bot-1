package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateService {
  public LocalDate getNow() {
    return DateUtil.now();
  }
}
