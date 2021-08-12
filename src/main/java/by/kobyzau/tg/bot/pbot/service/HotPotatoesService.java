package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface HotPotatoesService {

  boolean isHotPotatoesDay(long chatId, LocalDate localDate);

  Optional<Pidor> getLastTaker(LocalDate localDate, long chatId);

  Optional<LocalDateTime> getLastTakerDeadline(LocalDate localDate, long chatId);

  LocalDateTime setNewTaker(Pidor pidor);
}
