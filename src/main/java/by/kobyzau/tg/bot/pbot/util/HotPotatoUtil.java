package by.kobyzau.tg.bot.pbot.util;

import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class HotPotatoUtil {

  private static final int END_HOUR = 19;
  private final Selection<String> potatoesTakerMessage =
      new ConsistentSelection<>(
          "{0} - держатель <b>горячей картошечки\uD83E\uDD54</b>\nЧерез {1} он сгорит",
          "<b>Горячая картошечка\uD83E\uDD54</b> у {0}\nЧерез {1} он станет пидором",
          "{0} избавься от <b>горячей картошечки\uD83E\uDD54</b>, или через {1} ты станешь пидором",
          "{0} - если через {1} ты не выкинишь <b>горячую картошечку\uD83E\uDD54</b>, ты станешь пидором",
          "И <b>горячая картошечка\uD83E\uDD54</b> достаётся {0}\nОн должен её выкинуть в течение {1}",
          "{0} - лови <b>горячую картошечку\uD83E\uDD54</b>\nВыкинь её в течение {1}",
          "{0} - <b>горячая картошечка\uD83E\uDD54</b> у тебя\nЧерез {1} ты станешь пидором");

  public String getPotatoesTakerMessage() {
    return potatoesTakerMessage.next();
  }

  public LocalDateTime getDeadline(LocalDateTime fromTime) {
    int hour = fromTime.getHour();
    int minute = fromTime.getMinute();
    if (hour >= END_HOUR) {
      return fromTime;
    }
    int minutesLeft = hour * 60 + minute + (END_HOUR * 60 - hour * 60 - minute) / 2;
    int deadlineHour = minutesLeft / 60;
    int deadlineMinute = minutesLeft - 60 * deadlineHour;
    return LocalDateTime.of(
        fromTime.getYear(),
        fromTime.getMonth(),
        fromTime.getDayOfMonth(),
        deadlineHour,
        deadlineMinute);
  }
}
