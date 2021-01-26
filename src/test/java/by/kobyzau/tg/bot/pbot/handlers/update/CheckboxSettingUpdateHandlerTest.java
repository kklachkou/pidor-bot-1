package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.RuntimeExecutor;
import by.kobyzau.tg.bot.pbot.checker.BotActionAbstractTest;
import by.kobyzau.tg.bot.pbot.checker.BotTypeBotActionChecker;
import by.kobyzau.tg.bot.pbot.model.dto.ChatCheckboxSettingCommandDto;
import by.kobyzau.tg.bot.pbot.model.dto.CheckboxSettingCommandInlineDto;
import by.kobyzau.tg.bot.pbot.program.printer.SettingsCommandPrinter;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.service.FutureActionService;
import by.kobyzau.tg.bot.pbot.tg.action.AnswerCallbackBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.UUID;
import java.util.concurrent.Executor;

import static by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType.SETTING_ROOT;
import static by.kobyzau.tg.bot.pbot.service.ChatSettingsService.ChatCheckboxSettingType.GAMES_FREQUENT;
import static by.kobyzau.tg.bot.pbot.service.ChatSettingsService.ChatCheckboxSettingType.GDPR_MESSAGE_ENABLED;
import static by.kobyzau.tg.bot.pbot.service.FutureActionService.FutureActionType.ENABLE_SETTING;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CheckboxSettingUpdateHandlerTest extends BotActionAbstractTest {

  private final long chatId = 123;
  private final ChatSettingsService.ChatCheckboxSettingType realTimeType = GDPR_MESSAGE_ENABLED;
  private final ChatSettingsService.ChatCheckboxSettingType futureType = GAMES_FREQUENT;
  private User bot;
  private User calledUser;
  private final String id = UUID.randomUUID().toString().substring(SETTING_ROOT.getIdSize());

  @InjectMocks private CheckboxSettingUpdateHandler handler;

  @Mock private FutureActionService futureActionService;
  @Mock private ChatSettingsService chatSettingsService;
  @Mock private SettingsCommandPrinter settingsCommandPrinter;
  @Spy private Executor executor = new RuntimeExecutor();

  @Before
  public void init() {
    ReflectionTestUtils.setField(handler, "botUserName", "bot");
    bot = new User(0, "bot", true);
    bot.setUserName("bot");
    calledUser = new User(123, "caller", false);
  }

  @Test
  public void handle_realTime_noCallback() {
    // given
    Update update = new Update();
    doReturn(true).when(chatSettingsService).isEnabled(realTimeType, chatId);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertFalse(processed);
    verify(chatSettingsService, times(0)).setEnabled(realTimeType, chatId, false);
    verify(chatSettingsService, times(0)).setEnabled(realTimeType, chatId, true);
    verifyFutureActionNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handle_realTime_noPrevMessage() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();

    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(
        StringUtil.serialize(new CheckboxSettingCommandInlineDto(id, realTimeType)));
    update.setCallbackQuery(callbackQuery);
    doReturn(true).when(chatSettingsService).isEnabled(realTimeType, chatId);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertFalse(processed);
    verify(chatSettingsService, times(0)).setEnabled(realTimeType, chatId, false);
    verify(chatSettingsService, times(0)).setEnabled(realTimeType, chatId, true);
    verifyFutureActionNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handle_realTime_noData() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);

    callbackQuery.setFrom(calledUser);
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(true).when(chatSettingsService).isEnabled(realTimeType, chatId);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertFalse(processed);
    verify(chatSettingsService, times(0)).setEnabled(realTimeType, chatId, false);
    verify(chatSettingsService, times(0)).setEnabled(realTimeType, chatId, true);
    verifyFutureActionNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handle_realTime_anotherIndex() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);

    callbackQuery.setFrom(calledUser);
    CheckboxSettingCommandInlineDto dto = new CheckboxSettingCommandInlineDto(id, realTimeType);
    dto.setIndex(-1);
    callbackQuery.setData(StringUtil.serialize(dto));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(true).when(chatSettingsService).isEnabled(realTimeType, chatId);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertFalse(processed);
    verify(chatSettingsService, times(0)).setEnabled(realTimeType, chatId, false);
    verify(chatSettingsService, times(0)).setEnabled(realTimeType, chatId, true);
    verifyFutureActionNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handle_realtime_disable() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);

    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(
        StringUtil.serialize(new CheckboxSettingCommandInlineDto(id, realTimeType)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(true).when(chatSettingsService).isEnabled(realTimeType, chatId);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertTrue(processed);
    verify(chatSettingsService, times(1)).setEnabled(realTimeType, chatId, false);
    verify(chatSettingsService, times(0)).setEnabled(realTimeType, chatId, true);
    verifyFutureActionNotSaved();
    checkActions(new BotTypeBotActionChecker(AnswerCallbackBotAction.class));
  }

  @Test
  public void handle_realtime_enable() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);

    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(
        StringUtil.serialize(new CheckboxSettingCommandInlineDto(id, realTimeType)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(false).when(chatSettingsService).isEnabled(realTimeType, chatId);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertTrue(processed);
    verify(chatSettingsService, times(0)).setEnabled(realTimeType, chatId, false);
    verify(chatSettingsService, times(1)).setEnabled(realTimeType, chatId, true);
    verifyFutureActionNotSaved();
    checkActions(new BotTypeBotActionChecker(AnswerCallbackBotAction.class));
  }

  @Test
  public void handle_future_noCallback() {
    // given
    Update update = new Update();
    doReturn(true).when(chatSettingsService).isEnabled(futureType, chatId);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertFalse(processed);
    verify(chatSettingsService, times(0)).setEnabled(futureType, chatId, false);
    verify(chatSettingsService, times(0)).setEnabled(futureType, chatId, true);
    verifyFutureActionNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handle_future_noPrevMessage() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();

    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(
        StringUtil.serialize(new CheckboxSettingCommandInlineDto(id, futureType)));
    update.setCallbackQuery(callbackQuery);
    doReturn(true).when(chatSettingsService).isEnabled(futureType, chatId);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertFalse(processed);
    verify(chatSettingsService, times(0)).setEnabled(futureType, chatId, false);
    verify(chatSettingsService, times(0)).setEnabled(futureType, chatId, true);
    verifyFutureActionNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handle_future_noData() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);

    callbackQuery.setFrom(calledUser);
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(true).when(chatSettingsService).isEnabled(futureType, chatId);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertFalse(processed);
    verify(chatSettingsService, times(0)).setEnabled(futureType, chatId, false);
    verify(chatSettingsService, times(0)).setEnabled(futureType, chatId, true);
    verifyFutureActionNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handle_future_anotherIndex() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);

    callbackQuery.setFrom(calledUser);
    CheckboxSettingCommandInlineDto dto = new CheckboxSettingCommandInlineDto(id, futureType);
    dto.setIndex(-1);
    callbackQuery.setData(StringUtil.serialize(dto));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(true).when(chatSettingsService).isEnabled(futureType, chatId);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertFalse(processed);
    verify(chatSettingsService, times(0)).setEnabled(futureType, chatId, false);
    verify(chatSettingsService, times(0)).setEnabled(futureType, chatId, true);
    verifyFutureActionNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handle_future_disable() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);

    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(
        StringUtil.serialize(new CheckboxSettingCommandInlineDto(id, futureType)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(true).when(chatSettingsService).isEnabled(futureType, chatId);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertTrue(processed);
    verify(chatSettingsService, times(0)).setEnabled(futureType, chatId, false);
    verify(chatSettingsService, times(0)).setEnabled(futureType, chatId, true);
    verifySavedFutureAction(false);
    checkActions(new BotTypeBotActionChecker(AnswerCallbackBotAction.class));
  }

  @Test
  public void handle_future_enable() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);

    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(
        StringUtil.serialize(new CheckboxSettingCommandInlineDto(id, futureType)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(false).when(chatSettingsService).isEnabled(futureType, chatId);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertTrue(processed);
    verify(chatSettingsService, times(0)).setEnabled(futureType, chatId, false);
    verify(chatSettingsService, times(0)).setEnabled(futureType, chatId, true);
    verifySavedFutureAction(true);
    checkActions(new BotTypeBotActionChecker(AnswerCallbackBotAction.class));
  }

  private void verifyFutureActionNotSaved() {
    verify(futureActionService, times(0))
        .saveFutureActionData(
            ENABLE_SETTING,
            DateUtil.now().plusDays(1),
            StringUtil.serialize(new ChatCheckboxSettingCommandDto(chatId, futureType, false)));
    verify(futureActionService, times(0))
        .saveFutureActionData(
            ENABLE_SETTING,
            DateUtil.now().plusDays(1),
            StringUtil.serialize(new ChatCheckboxSettingCommandDto(chatId, futureType, true)));
  }

  private void verifySavedFutureAction(boolean willBeEnabled) {
    verify(futureActionService, times(1))
        .saveFutureActionData(
            ENABLE_SETTING,
            DateUtil.now().plusDays(1),
            StringUtil.serialize(
                new ChatCheckboxSettingCommandDto(chatId, futureType, willBeEnabled)));
    verify(futureActionService, times(0))
        .saveFutureActionData(
            ENABLE_SETTING,
            DateUtil.now().plusDays(1),
            StringUtil.serialize(
                new ChatCheckboxSettingCommandDto(chatId, futureType, !willBeEnabled)));
  }
}
