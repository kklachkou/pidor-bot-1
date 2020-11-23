package by.kobyzau.tg.bot.pbot.program.text.collector;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import by.kobyzau.tg.bot.pbot.program.text.EmptyText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;

public class ToTextCollectorTest {

  @Test
  public void emptyList_del() {
    // given
    List<Text> list = Collections.emptyList();

    // when
    Text result = list.stream().collect(new ToTextCollector(new SimpleText("-")));

    // then
    assertEquals(new EmptyText(), result);
  }

  @Test
  public void emptyList_noDel() {
    // given
    List<Text> list = Collections.emptyList();

    // when
    Text result = list.stream().collect(new ToTextCollector());

    // then
    assertEquals(new EmptyText(), result);
  }

  @Test
  public void singleList_del() {
    // given
    List<Text> list = Collections.singletonList(new SimpleText("One"));

    // when
    Text result = list.stream().collect(new ToTextCollector(new SimpleText("-")));

    // then
    assertEquals(new SimpleText("One"), result);
  }

  @Test
  public void singleList_noDel() {
    // given
    List<Text> list = Collections.singletonList(new SimpleText("One"));

    // when
    Text result = list.stream().collect(new ToTextCollector());

    // then
    assertEquals(new SimpleText("One"), result);
  }

  @Test
  public void doubleList_del() {
    // given
    List<Text> list = Arrays.asList(new SimpleText("One"), new SimpleText("Two"));

    // when
    Text result = list.stream().collect(new ToTextCollector(new SimpleText("-")));

    // then
    assertEquals(new SimpleText("One-Two"), result);
  }

  @Test
  public void doubleList_noDel() {
    // given
    List<Text> list = Arrays.asList(new SimpleText("One"), new SimpleText("Two"));

    // when
    Text result = list.stream().collect(new ToTextCollector());

    // then
    assertEquals(new SimpleText("OneTwo"), result);
  }

  @Test
  public void multiList_del() {
    // given
    List<Text> list =
        Arrays.asList(new SimpleText("One"), new SimpleText("Two"), new SimpleText("Three"));

    // when
    Text result = list.stream().collect(new ToTextCollector(new SimpleText("-")));

    // then
    assertEquals(new SimpleText("One-Two-Three"), result);
  }

  @Test
  public void multiList_noDel() {
    // given
    List<Text> list =
        Arrays.asList(new SimpleText("One"), new SimpleText("Two"), new SimpleText("Three"));

    // when
    Text result = list.stream().collect(new ToTextCollector());

    // then
    assertEquals(new SimpleText("OneTwoThree"), result);
  }
}
