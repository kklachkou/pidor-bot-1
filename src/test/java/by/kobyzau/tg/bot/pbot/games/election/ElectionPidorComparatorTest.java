package by.kobyzau.tg.bot.pbot.games.election;

import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ElectionPidorComparatorTest {

  private static final long CHAT_ID = 123;

  @Mock private ElectionService electionService;

  @Test
  public void compare_test() {
    // given
    LocalDate date = LocalDate.now();
    List<Pidor> pidors = new ArrayList<>();
    pidors.add(getPidor(5));
    pidors.add(getPidor(1));
    pidors.add(getPidor(2));
    pidors.add(getPidor(3));
    pidors.add(getPidor(4));
    pidors.add(getPidor(6));
    doReturn(0).when(electionService).getNumVotes(CHAT_ID, date, 1);
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, date, 2);
    doReturn(10).when(electionService).getNumVotes(CHAT_ID, date, 3);
    doReturn(2).when(electionService).getNumVotes(CHAT_ID, date, 4);
    doReturn(0).when(electionService).getNumVotes(CHAT_ID, date, 5);
    doReturn(5).when(electionService).getNumVotes(CHAT_ID, date, 6);

    // when
    pidors.sort(new ElectionPidorComparator(electionService));

    // then
    assertEquals(
        Arrays.asList(getPidor(3), getPidor(6), getPidor(2), getPidor(4), getPidor(1), getPidor(5)),
        pidors);
  }

  private Pidor getPidor(long id) {
    return Pidor.builder().id(id).chatId(CHAT_ID).fullname("Pidor" + id).tgId(id).build();
  }
}
