package by.kobyzau.tg.bot.pbot.collectors;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ReceiveUpdateCollector {

  void collectUpdate(Update update);

  Update poll();
}
