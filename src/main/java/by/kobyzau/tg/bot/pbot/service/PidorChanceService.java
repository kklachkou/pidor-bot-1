package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.model.Pidor;

import java.util.List;

public interface PidorChanceService {

  List<Pair<Pidor, Double>> calcChances(long chatId, int year);

}
