package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;

public interface PidorService {

  Optional<Pidor> getPidor(long chatId, long userId);

  Pidor createPidor(long chatId, User user);

  void updatePidor(Pidor pidor);

  List<Pidor> getByChat(long chatId);

  List<Long> getChatIds();

  void deletePidor(long chatId, long userId);
}
