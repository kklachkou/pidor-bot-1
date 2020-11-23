package by.kobyzau.tg.bot.pbot.repository;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestObjectRepository extends CrudRepository<Pidor, Long> {}
