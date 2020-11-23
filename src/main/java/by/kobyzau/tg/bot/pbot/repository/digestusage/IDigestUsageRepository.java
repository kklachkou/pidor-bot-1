package by.kobyzau.tg.bot.pbot.repository.digestusage;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import by.kobyzau.tg.bot.pbot.model.DigestUsage;

@Repository
public interface IDigestUsageRepository extends CrudRepository<DigestUsage, Long> {

  @Query("SELECT p FROM DigestUsage p WHERE p.type = ?1")
  List<DigestUsage> findByType(String type);
}
