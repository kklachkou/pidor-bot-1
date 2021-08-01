package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.checker.BotActionAbstractTest;
import by.kobyzau.tg.bot.pbot.checker.BotTypeBotActionChecker;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.ParsedCommand;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandlerFactory;
import by.kobyzau.tg.bot.pbot.handlers.command.parser.CommandParser;
import by.kobyzau.tg.bot.pbot.handlers.update.impl.CommandUpdateHandler;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.tg.action.ChatActionBotAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommandUpdateHandlerTest extends BotActionAbstractTest {

  private final long chatId = 123;
  private final long adminId = 54321;
  private final Command command = Command.PIDOR;
  private final String commandText = "command text";
  private final String messageText = "message text";

  @InjectMocks private CommandUpdateHandler handler;

  @Mock private Logger logger;

  @Mock private CommandHandlerFactory commandHandlerFactory;

  @Mock private CommandParser commandParser;

  @Mock private BotService botService;
  @Mock private CommandHandler commandHandler;

  @Before
  public void init() {
    ReflectionTestUtils.setField(handler, "adminUserId", adminId);
    doReturn(new ParsedCommand(command, commandText)).when(commandParser).parseCommand(anyString());
    doReturn(commandHandler).when(commandHandlerFactory).getHandler(command);
  }

  @Test
  public void handleUpdate_notValidMessage_noMessage() {
    // given
    Update update = new Update();

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertCommandNotProcessed();
  }

  @Test
  public void handleUpdate_notValidMessage_noMessageText() {
    // given
    Update update = new Update();
    update.setMessage(getMessage(null, adminId));
    doReturn(true).when(botService).isChatValid(chatId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertCommandNotProcessed();
  }

  @Test
  public void handleUpdate_notValidMessage_noUser() {
    // given
    Update update = new Update();
    update.setMessage(getMessage(messageText, null));
    doReturn(true).when(botService).isChatValid(chatId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertCommandNotProcessed();
  }

  @Test
  public void handleUpdate_notValidMessage_chatIsNotValid() {
    // given
    Update update = new Update();
    update.setMessage(getMessage(messageText, adminId));
    doReturn(false).when(botService).isChatValid(chatId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertCommandNotProcessed();
  }

  @Test
  public void handleUpdate_hiddenCommand_admin() {
    // given
    Update update = new Update();
    update.setMessage(getMessage(messageText, adminId));
    doReturn(true).when(botService).isChatValid(chatId);
    doReturn(new ParsedCommand(Command.TEST, commandText))
        .when(commandParser)
        .parseCommand(anyString());
    doReturn(commandHandler).when(commandHandlerFactory).getHandler(Command.TEST);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertTrue(result);
    assertCommandProcessed(update.getMessage());
  }

  @Test
  public void handleUpdate_hiddenCommand_nonAdmin() {
    // given
    Update update = new Update();
    update.setMessage(getMessage(messageText, adminId + 1));
    doReturn(true).when(botService).isChatValid(chatId);
    doReturn(new ParsedCommand(Command.TEST, commandText))
        .when(commandParser)
        .parseCommand(anyString());
    doReturn(commandHandler).when(commandHandlerFactory).getHandler(Command.TEST);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertCommandNotProcessed();
  }

  @Test
  public void handleUpdate_regularCommand_admin() {
    // given
    Update update = new Update();
    update.setMessage(getMessage(messageText, adminId));
    doReturn(true).when(botService).isChatValid(chatId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertTrue(result);
    assertCommandProcessed(update.getMessage());
  }

  @Test
  public void handleUpdate_regularCommand_nonAdmin() {
    // given
    Update update = new Update();
    update.setMessage(getMessage(messageText, adminId + 1));
    doReturn(true).when(botService).isChatValid(chatId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertTrue(result);
    assertCommandProcessed(update.getMessage());
  }

  private Message getMessage(String messageText, Long userId) {
    Message message = new Message();
    message.setChat(new Chat(chatId, "group"));
    message.setText(messageText);
    if (userId != null) {
      message.setFrom(new User(userId, "user", false));
    }
    return message;
  }

  private void assertCommandNotProcessed() {
    verify(commandHandler, times(0)).processCommand(any(), any());
    checkNoAnyActions();
  }

  private void assertCommandProcessed(Message message) {
    verify(commandHandler, times(1)).processCommand(message, commandText);
    checkActions(new BotTypeBotActionChecker(ChatActionBotAction.class));
  }
}
