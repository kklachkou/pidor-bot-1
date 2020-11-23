package by.kobyzau.tg.bot.pbot.program.shuffler;

import by.kobyzau.tg.bot.pbot.program.list.ListProvider;
import by.kobyzau.tg.bot.pbot.program.list.ListWrapper;
import by.kobyzau.tg.bot.pbot.program.list.SingletonListProvider;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ListWrapperTest {

  @Test
  public void getList() {
    // given
    ListProvider<String> p1 =
        new ListWrapper<>(
            new SingletonListProvider<>("p1 One"), new SingletonListProvider<>("p1 Two"));

    ListProvider<String> p2 =
        new ListWrapper<>(
            new SingletonListProvider<>("p2 One"), new SingletonListProvider<>("p2 Two"));

    // when
    List<String> list = new ListWrapper<>(p1, p2).getList();

    // then
    Assert.assertEquals(Arrays.asList("p1 One", "p1 Two", "p2 One", "p2 Two"), list);
  }
}
