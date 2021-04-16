package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.RuntimeExecutor;
import by.kobyzau.tg.bot.pbot.bots.game.election.ElectionFinalizer;
import by.kobyzau.tg.bot.pbot.checker.BotActionAbstractTest;
import by.kobyzau.tg.bot.pbot.checker.BotTypeBotActionChecker;
import by.kobyzau.tg.bot.pbot.games.election.stat.ElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.model.dto.VoteInlineMessageInlineDto;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.AnswerCallbackBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executor;

import static by.kobyzau.tg.bot.pbot.service.ChatSettingsService.ChatCheckboxSettingType.ELECTION_HIDDEN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ElectionUpdateHandlerTest extends BotActionAbstractTest {

  @Rule public TestName testName = new TestName();

  private final long chatId = 123;
  private final long callerId = 1;
  private final long targetId = 2;
  private User bot;

  @InjectMocks private ElectionUpdateHandler handler;

  @Mock private ElectionService electionService;
  @Mock private PidorService pidorService;
  @Mock private DailyPidorRepository dailyPidorRepository;
  @Mock private ElectionFinalizer electionFinalizer;
  @Mock private ChatSettingsService chatSettingsService;
  @Mock private ElectionStatPrinter fullWithNumLeftElectionStatPrinter;
  @Mock private ElectionStatPrinter hiddenElectionStatPrinter;
  @Spy private Executor executor = new RuntimeExecutor();

  @Before
  public void init() {
    ReflectionTestUtils.setField(handler, "botUserName", "bot");
    bot = new User(0L, "bot", true);
    bot.setUserName("bot");
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.of(new Pidor(callerId, chatId, "caller")))
        .when(pidorService)
        .getPidor(chatId, callerId);
    doReturn(Optional.of(new Pidor(targetId, chatId, "target")))
        .when(pidorService)
        .getPidor(chatId, targetId);
    doReturn(
            Arrays.asList(
                new Pidor(targetId, chatId, "target"), new Pidor(callerId, chatId, "caller")))
        .when(pidorService)
        .getByChat(chatId);
    doReturn(true).when(electionService).canUserVote(chatId, callerId);
    doReturn(true).when(electionService).isElectionDay(chatId, DateUtil.now());
    System.out.println("************ START " + testName.getMethodName() + " *************");
  }

  @After
  public void after() {
    System.out.println("************ END *************");
  }

  @Test
  public void handleUpdate_noCallback() {
    // given
    setupPidorOfTheDay();
    Update update = new Update();

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkNoAnyActions();
    assertNotFinalized();
    assertNotPrinter();
    assertNotSavedVote();
    assertFalse(result);
  }

  @Test
  public void handleUpdate_noData() {
    // given
    setupPidorOfTheDay();
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);
    User calledUser = new User(callerId, "caller", false);
    callbackQuery.setFrom(calledUser);
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkNoAnyActions();
    assertNotFinalized();
    assertNotSavedVote();
    assertNotPrinter();
    assertFalse(result);
  }

  @Test
  public void handleUpdate_anotherCaller() {
    // given
    setupPidorOfTheDay();
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);
    User calledUser = new User(targetId, "caller", false);
    User callerUser = new User(callerId, "caller", false);
    Message replyMessage = new Message();
    replyMessage.setFrom(callerUser);
    prevMessage.setReplyToMessage(replyMessage);
    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageInlineDto("id", targetId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(new BotTypeBotActionChecker(AnswerCallbackBotAction.class));
    assertNotFinalized();
    assertNotPrinter();
    assertNotSavedVote();
    assertTrue(result);
  }

  @Test
  public void handleUpdate_anotherIndex() {
    // given
    setupPidorOfTheDay();
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);
    User calledUser = new User(callerId, "caller", false);
    Message replyMessage = new Message();
    replyMessage.setFrom(calledUser);
    prevMessage.setReplyToMessage(replyMessage);
    callbackQuery.setFrom(calledUser);
    VoteInlineMessageInlineDto dto = new VoteInlineMessageInlineDto("id", targetId);
    dto.setIndex(SerializableInlineType.VOTE.getIndex() + 1);
    callbackQuery.setData(StringUtil.serialize(dto));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkNoAnyActions();
    assertNotFinalized();
    assertNotPrinter();
    assertNotSavedVote();
    assertFalse(result);
  }

  @Test
  public void handleUpdate_hasPidorOfToday() {
    // given
    setupPidorOfTheDay();
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);
    User calledUser = new User(callerId, "caller", false);
    Message replyMessage = new Message();
    replyMessage.setFrom(calledUser);
    prevMessage.setReplyToMessage(replyMessage);
    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageInlineDto("id", targetId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(new BotTypeBotActionChecker(EditMessageBotAction.class));
    assertNotFinalized();
    assertNotPrinter();
    assertNotSavedVote();
    assertTrue(result);
  }

  @Test
  public void handleUpdate_noTargetPidor() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);
    User calledUser = new User(callerId, "caller", false);
    callbackQuery.setFrom(calledUser);
    Message replyMessage = new Message();
    replyMessage.setFrom(calledUser);
    prevMessage.setReplyToMessage(replyMessage);
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageInlineDto("id", targetId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(Optional.empty()).when(pidorService).getPidor(chatId, targetId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(new BotTypeBotActionChecker(EditMessageBotAction.class));
    assertNotFinalized();
    assertNotPrinter();
    assertNotSavedVote();
    assertTrue(result);
  }

  @Test
  public void handleUpdate_noCallerPidor() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);
    User calledUser = new User(callerId, "caller", false);
    Message replyMessage = new Message();
    replyMessage.setFrom(calledUser);
    prevMessage.setReplyToMessage(replyMessage);
    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageInlineDto("id", targetId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(Optional.empty()).when(pidorService).getPidor(chatId, callerId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(new BotTypeBotActionChecker(EditMessageBotAction.class));
    assertNotFinalized();
    assertNotPrinter();
    assertNotSavedVote();
    assertTrue(result);
  }

  @Test
  public void handleUpdate_hiddenElection_notFinalized() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);
    User calledUser = new User(callerId, "caller", false);
    Message replyMessage = new Message();
    replyMessage.setFrom(calledUser);
    prevMessage.setReplyToMessage(replyMessage);
    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageInlineDto("id", targetId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(5).when(electionService).getNumToVote(chatId);
    doReturn(true).when(chatSettingsService).isEnabled(ELECTION_HIDDEN, chatId );

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(
            new BotTypeBotActionChecker(EditMessageBotAction.class),
            new BotTypeBotActionChecker(AnswerCallbackBotAction.class));
    assertNotFinalized();
    assertPrinted(true);
    assertSavedVote();
    assertTrue(result);
  }


  @Test
  public void handleUpdate_notFinalized() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);
    User calledUser = new User(callerId, "caller", false);
    Message replyMessage = new Message();
    replyMessage.setFrom(calledUser);
    prevMessage.setReplyToMessage(replyMessage);
    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageInlineDto("id", targetId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(5).when(electionService).getNumToVote(chatId);
    doReturn(false).when(chatSettingsService).isEnabled(ELECTION_HIDDEN, chatId );

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(
            new BotTypeBotActionChecker(EditMessageBotAction.class),
            new BotTypeBotActionChecker(AnswerCallbackBotAction.class));
    assertNotFinalized();
    assertPrinted(false);
    assertSavedVote();
    assertTrue(result);
  }

  @Test
  public void handleUpdate_finalized() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);
    User calledUser = new User(callerId, "caller", false);
    Message replyMessage = new Message();
    replyMessage.setFrom(calledUser);
    prevMessage.setReplyToMessage(replyMessage);
    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageInlineDto("id", targetId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(
            new BotTypeBotActionChecker(EditMessageBotAction.class),
            new BotTypeBotActionChecker(AnswerCallbackBotAction.class));
    assertFinalized();
    assertNotPrinter();
    assertSavedVote();
    assertTrue(result);
  }


  @Test
  public void handleUpdate_notElectionDay() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);
    User calledUser = new User(callerId, "caller", false);
    Message replyMessage = new Message();
    replyMessage.setFrom(calledUser);
    prevMessage.setReplyToMessage(replyMessage);
    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageInlineDto("id", targetId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(false).when(electionService).isElectionDay(chatId, DateUtil.now());

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkNoAnyActions();
    assertNotFinalized();
    assertNotPrinter();
    assertNotSavedVote();
    assertFalse(result);
  }

  private void setupPidorOfTheDay() {
    doReturn(Optional.of(new DailyPidor()))
        .when(dailyPidorRepository)
        .getByChatAndDate(chatId, DateUtil.now());
  }

  private void assertNotFinalized() {
    verify(electionFinalizer, times(0)).finalize(chatId);
  }

  private void assertFinalized() {
    verify(electionFinalizer, times(1)).finalize(chatId);
  }

  private void assertPrinted(boolean isHidden) {
    verify(fullWithNumLeftElectionStatPrinter, times(isHidden ? 0 : 1)).printInfo(chatId);
    verify(hiddenElectionStatPrinter, times(isHidden ? 1 : 0)).printInfo(chatId);
  }

  private void assertNotPrinter() {
    verify(fullWithNumLeftElectionStatPrinter, times(0)).printInfo(chatId);
    verify(hiddenElectionStatPrinter, times(0)).printInfo(chatId);
  }

  private void assertSavedVote() {
    verify(electionService, times(1)).saveVote(chatId, callerId, targetId);
  }

  private void assertNotSavedVote() {
    verify(electionService, times(0)).saveVote(chatId, callerId, targetId);
  }
}
