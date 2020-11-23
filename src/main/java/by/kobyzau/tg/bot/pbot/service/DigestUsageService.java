package by.kobyzau.tg.bot.pbot.service;

import java.util.List;

import by.kobyzau.tg.bot.pbot.model.DigestUsageType;

public interface DigestUsageService {

  void saveDigestUsage(DigestUsageType type, String digest);

  List<String> getTopDigestsByType(DigestUsageType type, int top);
}
