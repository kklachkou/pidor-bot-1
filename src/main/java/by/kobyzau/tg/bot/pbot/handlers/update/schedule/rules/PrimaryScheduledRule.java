package by.kobyzau.tg.bot.pbot.handlers.update.schedule.rules;

import by.kobyzau.tg.bot.pbot.handlers.update.schedule.ScheduledItem;
import java.time.LocalDate;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//@Component
@Profile("dev")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PrimaryScheduledRule implements ScheduledRule {
    @Override
    public boolean isMatch(long chatId, LocalDate localDate) {
        return true;
    }

    @Override
    public ScheduledItem getItem() {
        return ScheduledItem.POTATOES;
    }
}
