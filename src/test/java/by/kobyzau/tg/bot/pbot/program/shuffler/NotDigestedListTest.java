package by.kobyzau.tg.bot.pbot.program.shuffler;

import by.kobyzau.tg.bot.pbot.program.digest.StringDigestCalc;
import by.kobyzau.tg.bot.pbot.program.list.NotDigestedList;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NotDigestedListTest {

  private static final String WITH_DIGEST = "With digest";
  private static final String WITHOUT_DIGEST = "Without digest";

  @Test
  public void getList_emptyDigestList() {
    // given
    List<String> list = Arrays.asList(WITH_DIGEST, WITHOUT_DIGEST);

    // when
    List<String> result =
        new NotDigestedList<>(list, new StringDigestCalc(), Collections.emptyList()).getList();

    // then
    Assert.assertEquals(list, result);
  }

  @Test
  public void getList_haveDigestList() {
    // given
    List<String> list = Arrays.asList(WITH_DIGEST, WITHOUT_DIGEST);

    // when
    List<String> result =
        new NotDigestedList<>(
                list,
                new StringDigestCalc(),
                Collections.singletonList(new StringDigestCalc().getDigest(WITH_DIGEST)))
            .getList();

    // then
    Assert.assertEquals(Collections.singletonList(WITHOUT_DIGEST), result);
  }
}
