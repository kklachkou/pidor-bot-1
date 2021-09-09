package by.kobyzau.tg.bot.pbot.util;

import org.junit.Test;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

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

  @Test
  public void canPinMessage_creator() {
    // given
    ChatMember chatMember = new ChatMemberOwner();

    // when
    boolean canPin = TGUtil.canPinMessage(chatMember);

    // then
    assertTrue(canPin);
  }

  @Test
  public void canPinMessage_administrator_cannotPin() {
    // given
    ChatMember chatMember = ChatMemberAdministrator.builder().canPinMessages(false).build();

    // when
    boolean canPin = TGUtil.canPinMessage(chatMember);

    // then
    assertFalse(canPin);
  }

  @Test
  public void canPinMessage_administrator_canPin() {
    // given
    ChatMember chatMember = ChatMemberAdministrator.builder().canPinMessages(true).build();

    // when
    boolean canPin = TGUtil.canPinMessage(chatMember);

    // then
    assertTrue(canPin);
  }

  @Test
  public void canPinMessage_restricted_canPin() {
    // given
    ChatMember chatMember = ChatMemberRestricted.builder().canPinMessages(true).build();

    // when
    boolean canPin = TGUtil.canPinMessage(chatMember);

    // then
    assertTrue(canPin);
  }

  @Test
  public void canPinMessage_restricted_cannotPin() {
    // given
    ChatMember chatMember = ChatMemberRestricted.builder().canPinMessages(false).build();

    // when
    boolean canPin = TGUtil.canPinMessage(chatMember);

    // then
    assertFalse(canPin);
  }

  @Test
  public void canPinMessage_another() {
    // given
    ChatMember chatMember = mock(ChatMember.class);
    doReturn("another").when(chatMember).getStatus();

    // when
    boolean canPin = TGUtil.canPinMessage(chatMember);

    // then
    assertFalse(canPin);
  }

  @Test
  public void canDeleteMessage_creator() {
    // given
    ChatMember chatMember = new ChatMemberOwner();

    // when
    boolean canPin = TGUtil.canDeleteMessage(chatMember);

    // then
    assertTrue(canPin);
  }

  @Test
  public void canDeleteMessage_administrator_cannotDelete() {
    // given
    ChatMember chatMember = ChatMemberAdministrator.builder().canDeleteMessages(false).build();

    // when
    boolean canPin = TGUtil.canDeleteMessage(chatMember);

    // then
    assertFalse(canPin);
  }

  @Test
  public void canDeleteMessage_administrator_canDelete() {
    // given
    ChatMember chatMember = ChatMemberAdministrator.builder().canDeleteMessages(true).build();

    // when
    boolean canPin = TGUtil.canDeleteMessage(chatMember);

    // then
    assertTrue(canPin);
  }

  @Test
  public void canDeleteMessage_another() {
    // given
    ChatMember chatMember = mock(ChatMember.class);
    doReturn("another").when(chatMember).getStatus();

    // when
    boolean canPin = TGUtil.canDeleteMessage(chatMember);

    // then
    assertFalse(canPin);
  }

  @Test
  public void cancanSendOtherMessages_creator() {
    // given
    ChatMember chatMember = new ChatMemberOwner();

    // when
    boolean canPin = TGUtil.canSendOtherMessages(chatMember);

    // then
    assertTrue(canPin);
  }

  @Test
  public void cancanSendOtherMessages_administrator() {
    // given
    ChatMember chatMember = new ChatMemberAdministrator();

    // when
    boolean canPin = TGUtil.canSendOtherMessages(chatMember);

    // then
    assertTrue(canPin);
  }

  @Test
  public void cancanSendOtherMessages_member() {
    // given
    ChatMember chatMember = new ChatMemberMember();

    // when
    boolean canPin = TGUtil.canSendOtherMessages(chatMember);

    // then
    assertTrue(canPin);
  }

  @Test
  public void cancanSendOtherMessages_restricted_cannotSend() {
    // given
    ChatMember chatMember = ChatMemberRestricted.builder().canSendOtherMessages(false).build();

    // when
    boolean canPin = TGUtil.canSendOtherMessages(chatMember);

    // then
    assertFalse(canPin);
  }

  @Test
  public void cancanSendOtherMessages_restricted_canSend() {
    // given
    ChatMember chatMember = ChatMemberRestricted.builder().canSendOtherMessages(true).build();

    // when
    boolean canPin = TGUtil.canSendOtherMessages(chatMember);

    // then
    assertTrue(canPin);
  }
}
