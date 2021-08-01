package by.kobyzau.tg.bot.pbot.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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

  public static <T> List<T> getRandomListByDay(Collection<T> c, LocalDate date) {
    List<T> result = new ArrayList<>();
    List<T> iterationList = new ArrayList<>(c);
    for (int iteration = 0; iteration < c.size(); iteration++) {
      int newIndex =
          Objects.hash(
                  date.getDayOfYear(),
                  date.getMonthValue(),
                  date.getDayOfWeek().getValue(),
                  iterationList.size())
              % iterationList.size();
      result.add(iterationList.get(newIndex));
      iterationList.remove(newIndex);
    }
    return result;
  }

  public static <T> T getItem(List<T> list, int index) {
    int size = list.size();
    return list.get(index % size);
  }
}
