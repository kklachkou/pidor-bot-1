package by.kobyzau.tg.bot.pbot.util;

import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.Assert.assertEquals;

public class TGUtilTest {

  @Test
  public void getUsername_null() {
    // given
    User user = new User();

    // when
    String username = TGUtil.getUsername(user);

    // then
    assertEquals("", username);
  }

  @Test
  public void getUsername_empty() {
    // given
    User user = new User();
    user.setUserName("");

    // when
    String username = TGUtil.getUsername(user);

    // then
    assertEquals("", username);
  }

  @Test
  public void getUsername_name() {
    // given
    User user = new User();
    user.setUserName("username");

    // when
    String username = TGUtil.getUsername(user);

    // then
    assertEquals("username", username);
  }

  @Test
  public void getUsername_nameWithBelarus() {
    // given
    User user = new User();
    user.setUserName("username\uD83C\uDDE7\uD83C\uDDFE");

    // when
    String username = TGUtil.getUsername(user);

    // then
    assertEquals("username\uD83D\uDCA9", username);
  }

  @Test
  public void getFullName_onlyFirstName() {
    // given
    User user = new User();
    user.setFirstName("firstName");

    // when
    String fullName = TGUtil.getFullName(user);

    // then
    assertEquals("firstName", fullName);
  }

  @Test
  public void getFullName_firstAndLastNames() {
    // given
    User user = new User();
    user.setFirstName("firstName");
    user.setLastName("lastName");

    // when
    String fullName = TGUtil.getFullName(user);

    // then
    assertEquals("firstName lastName", fullName);
  }

  @Test
  public void getFullName_firstAndLastNames_withBelarus() {
    // given
    User user = new User();
    user.setFirstName("firstName\uD83C\uDDE7\uD83C\uDDFE");
    user.setLastName("lastName\uD83C\uDDE7\uD83C\uDDFE");

    // when
    String fullName = TGUtil.getFullName(user);

    // then
    assertEquals("firstName\uD83D\uDCA9 lastName\uD83D\uDCA9", fullName);
  }
}
