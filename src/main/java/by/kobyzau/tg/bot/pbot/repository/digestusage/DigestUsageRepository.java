package by.kobyzau.tg.bot.pbot.repository.digestusage;

import java.util.List;

import by.kobyzau.tg.bot.pbot.model.DigestUsage;
import by.kobyzau.tg.bot.pbot.repository.CrudRepository;

public interface DigestUsageRepository extends CrudRepository<DigestUsage> {

  List<DigestUsage> findByType(String type);
}
