package by.kobyzau.tg.bot.pbot.service;

import java.util.List;
import java.util.Optional;

import org.telegram.telegrambots.meta.api.objects.User;

import by.kobyzau.tg.bot.pbot.model.Pidor;

public interface PidorService {

  Optional<Pidor> getPidor(long chatId, int userId);

  Pidor createPidor(long chatId, User user);

  void updatePidor(Pidor pidor);

  List<Pidor> getByChat(long chatId);
}
