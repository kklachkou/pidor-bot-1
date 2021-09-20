package by.kobyzau.tg.bot.pbot.util.helper;

import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DateHelper {

  public LocalDate now() {
    return DateUtil.now();
  }

  public LocalDateTime currentTime() {
    return DateUtil.currentTime();
  }
}
