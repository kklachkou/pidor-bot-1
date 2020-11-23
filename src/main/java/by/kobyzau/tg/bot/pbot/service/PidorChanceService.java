package by.kobyzau.tg.bot.pbot.service;

import java.util.List;

import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.model.Pidor;

public interface PidorChanceService {

  List<Pair<Pidor, Integer>> calcChances(long chatId, int year);

}
