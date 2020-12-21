package by.kobyzau.tg.bot.pbot.repository.custom;

import by.kobyzau.tg.bot.pbot.model.CustomDailyUserData;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface CustomDailyDataRepository extends CrudRepository<CustomDailyUserData> {

  List<CustomDailyUserData> getByChat(long chatId);


  List<CustomDailyUserData> getByChatAndDate(long chatId, LocalDate localDate);
}
