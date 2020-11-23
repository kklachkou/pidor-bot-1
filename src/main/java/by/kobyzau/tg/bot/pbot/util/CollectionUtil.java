package by.kobyzau.tg.bot.pbot.util;

import java.util.*;

public class CollectionUtil {

  private static final Random RANDOM = new Random();

  public static <T> List<T> join(List<T> c1, List<T> c2) {
    List<T> list = new ArrayList<>();
    list.addAll(c1);
    list.addAll(c2);
    return list;
  }

  public static int size(Collection<?> c) {
    if (isEmpty(c)) {
      return 0;
    }
    return c.size();
  }

  public static boolean isNotEmpty(Collection<?> c) {
    return !isEmpty(c);
  }

  public static boolean isEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  public static <T> T getRandomValue(Collection<T> c) {
    if (isEmpty(c)) {
      throw new RuntimeException("Empty collection in getRandomValue");
    }
    return getRandomList(c).get(0);
  }

  public static <T> List<T> getRandomList(Collection<T> c) {
    List<T> list = new ArrayList<>(c);
    Collections.shuffle(list, RANDOM);
    return list;
  }
}
