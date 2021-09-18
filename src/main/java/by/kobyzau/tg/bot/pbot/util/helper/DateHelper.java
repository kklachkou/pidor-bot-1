package by.kobyzau.tg.bot.pbot.util.helper;

import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateHelper {

  public LocalDate now() {
    return DateUtil.now();
  }
}
