package by.kobyzau.tg.bot.pbot;

import by.kobyzau.tg.bot.pbot.model.dto.AssassinInlineMessageDto;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class TempTest {

  @Test
  public void instant() {
    UUID uuid = UUID.randomUUID();
    System.out.println(uuid.toString().substring(19));
    String string = StringUtil.serialize(
            new AssassinInlineMessageDto(uuid.toString().substring(19), 0, 0));
    System.out.println(string);
    Assert.assertEquals(0, string.getBytes().length);
  }
}
