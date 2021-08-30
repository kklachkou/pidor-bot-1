package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.bots.PidorBot;
import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.service.impl.BotServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberOwner;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberRestricted;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class BotServiceTest {

  @Mock private TelegramService telegramService;

  @Mock private BotActionCollector botActionCollector;

  @Mock private Logger logger;
  @Mock private PidorBot pidorBot;

  @InjectMocks private BotService botService = new BotServiceImpl();

  private static final long CHAT_ID = 100;
  private static final long ADMIN_ID = 123;
  private static final User BOT = new User(1L, "botName", true);

  @Before
  public void init() {
    ReflectionTestUtils.setField(botService, "adminUserId", ADMIN_ID);
  }

  @Test
  public void canPinMessage_noMe() {
    // given
    doReturn(Optional.empty()).when(telegramService).getMe();

    // when
    boolean canPin = botService.canPinMessage(CHAT_ID);

    // then
    assertFalse(canPin);
  }

  @Test
  public void canPinMessage_notChatMember() {
    // given
    doReturn(Optional.of(BOT)).when(telegramService).getMe();
    doReturn(Optional.empty()).when(telegramService).getChatMember(CHAT_ID, BOT.getId());

    // when
    boolean canPin = botService.canPinMessage(CHAT_ID);

    // then
    assertFalse(canPin);
  }

  @Test
  public void canPinMessage_creator() {
    // given
    doReturn(Optional.of(BOT)).when(telegramService).getMe();
    ChatMember chatMember = new ChatMemberOwner();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(CHAT_ID, BOT.getId());

    // when
    boolean canPin = botService.canPinMessage(CHAT_ID);

    // then
    assertTrue(canPin);
  }

  @Test
  public void canPinMessage_administrator_cannotPin() {
    // given
    doReturn(Optional.of(BOT)).when(telegramService).getMe();
    ChatMember chatMember = ChatMemberAdministrator.builder().canPinMessages(false).build();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(CHAT_ID, BOT.getId());

    // when
    boolean canPin = botService.canPinMessage(CHAT_ID);

    // then
    assertFalse(canPin);
  }

  @Test
  public void canPinMessage_administrator_canPin() {
    // given
    doReturn(Optional.of(BOT)).when(telegramService).getMe();
    ChatMember chatMember = ChatMemberAdministrator.builder().canPinMessages(true).build();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(CHAT_ID, BOT.getId());

    // when
    boolean canPin = botService.canPinMessage(CHAT_ID);

    // then
    assertTrue(canPin);
  }

  @Test
  public void canPinMessage_restricted_canPin() {
    // given
    doReturn(Optional.of(BOT)).when(telegramService).getMe();
    ChatMember chatMember = ChatMemberRestricted.builder().canPinMessages(true).build();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(CHAT_ID, BOT.getId());

    // when
    boolean canPin = botService.canPinMessage(CHAT_ID);

    // then
    assertTrue(canPin);
  }

  @Test
  public void canPinMessage_restricted_cannotPin() {
    // given
    doReturn(Optional.of(BOT)).when(telegramService).getMe();
    ChatMember chatMember = ChatMemberRestricted.builder().canPinMessages(false).build();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(CHAT_ID, BOT.getId());

    // when
    boolean canPin = botService.canPinMessage(CHAT_ID);

    // then
    assertFalse(canPin);
  }

  @Test
  public void canPinMessage_another() {
    // given
    doReturn(Optional.of(BOT)).when(telegramService).getMe();
    ChatMember chatMember = mock(ChatMember.class);
    doReturn("another").when(chatMember).getStatus();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(CHAT_ID, BOT.getId());

    // when
    boolean canPin = botService.canPinMessage(CHAT_ID);

    // then
    assertFalse(canPin);
  }

  @Test
  public void canDeleteMessage_noMe() {
    // given
    doReturn(Optional.empty()).when(telegramService).getMe();

    // when
    boolean canPin = botService.canDeleteMessage(CHAT_ID);

    // then
    assertFalse(canPin);
  }

  @Test
  public void canDeleteMessage_notChatMember() {
    // given
    doReturn(Optional.of(BOT)).when(telegramService).getMe();
    doReturn(Optional.empty()).when(telegramService).getChatMember(CHAT_ID, BOT.getId());

    // when
    boolean canPin = botService.canDeleteMessage(CHAT_ID);

    // then
    assertFalse(canPin);
  }

  @Test
  public void canDeleteMessage_creator() {
    // given
    doReturn(Optional.of(BOT)).when(telegramService).getMe();
    ChatMember chatMember = new ChatMemberOwner();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(CHAT_ID, BOT.getId());

    // when
    boolean canPin = botService.canDeleteMessage(CHAT_ID);

    // then
    assertTrue(canPin);
  }

  @Test
  public void canDeleteMessage_administrator_cannotDelete() {
    // given
    doReturn(Optional.of(BOT)).when(telegramService).getMe();
    ChatMember chatMember = ChatMemberAdministrator.builder().canDeleteMessages(false).build();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(CHAT_ID, BOT.getId());

    // when
    boolean canPin = botService.canDeleteMessage(CHAT_ID);

    // then
    assertFalse(canPin);
  }

  @Test
  public void canDeleteMessage_administrator_canDelete() {
    // given
    doReturn(Optional.of(BOT)).when(telegramService).getMe();
    ChatMember chatMember = ChatMemberAdministrator.builder().canDeleteMessages(true).build();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(CHAT_ID, BOT.getId());

    // when
    boolean canPin = botService.canDeleteMessage(CHAT_ID);

    // then
    assertTrue(canPin);
  }

  @Test
  public void canDeleteMessage_another() {
    // given
    doReturn(Optional.of(BOT)).when(telegramService).getMe();
    ChatMember chatMember = mock(ChatMember.class);
    doReturn("another").when(chatMember).getStatus();
    doReturn(Optional.of(chatMember)).when(telegramService).getChatMember(CHAT_ID, BOT.getId());

    // when
    boolean canPin = botService.canDeleteMessage(CHAT_ID);

    // then
    assertFalse(canPin);
  }

  @Test
  public void isChatValid_admin() {
    // when
    boolean isValid = botService.isChatValid(ADMIN_ID);

    // then
    assertTrue(isValid);
  }

  @Test
  public void isChatValid_noChat() {
    // given
    doReturn(Optional.empty()).when(telegramService).getChat(CHAT_ID);

    // when
    boolean isValid = botService.isChatValid(CHAT_ID);

    // then
    assertFalse(isValid);
  }

  @Test
  public void isChatValid_hasPrivateChat() {
    // given
    Chat chat = new Chat();
    chat.setId(CHAT_ID);
    chat.setType("private");
    doReturn(Optional.of(chat)).when(telegramService).getChat(CHAT_ID);

    // when
    boolean isValid = botService.isChatValid(CHAT_ID);

    // then
    assertFalse(isValid);
  }

  @Test
  public void isChatValid_hasGroupChat() {
    // given
    Chat chat = new Chat();
    chat.setType("group");
    doReturn(Optional.of(chat)).when(telegramService).getChat(CHAT_ID);

    // when
    boolean isValid = botService.isChatValid(CHAT_ID);

    // then
    assertTrue(isValid);
  }

  @Test
  public void isChatValid_hasSuperGroupChat() {
    // given
    Chat chat = new Chat();
    chat.setType("supergroup");
    doReturn(Optional.of(chat)).when(telegramService).getChat(CHAT_ID);

    // when
    boolean isValid = botService.isChatValid(CHAT_ID);

    // then
    assertTrue(isValid);
  }

  @Test
  public void isChatValid_isAdminChat() {
    // given
    Chat chat = new Chat();
    chat.setType("private");
    chat.setId(ADMIN_ID);

    // when
    boolean isValid = botService.isChatValid(chat);

    // then
    assertTrue(isValid);
  }

  @Test
  public void isChatValid_isPrivateChat() {
    // given
    Chat chat = new Chat();
    chat.setId(CHAT_ID);
    chat.setType("private");

    // when
    boolean isValid = botService.isChatValid(chat);

    // then
    assertFalse(isValid);
  }

  @Test
  public void isChatValid_isGroupChat() {
    // given
    Chat chat = new Chat();
    chat.setType("group");

    // when
    boolean isValid = botService.isChatValid(chat);

    // then
    assertTrue(isValid);
  }

  @Test
  public void isChatValid_isSuperGroupChat() {
    // given
    Chat chat = new Chat();
    chat.setType("supergroup");

    // when
    boolean isValid = botService.isChatValid(chat);

    // then
    assertTrue(isValid);
  }
}
