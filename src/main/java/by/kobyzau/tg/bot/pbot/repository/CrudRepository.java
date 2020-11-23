package by.kobyzau.tg.bot.pbot.repository;

import java.util.List;

public interface CrudRepository<T> {

  long create(T obj);

  void update(T obj);

  T get(long id);

  List<T> getAll();

  void delete(long id);
}
