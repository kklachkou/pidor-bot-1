package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.RuntimeExecutor;
import by.kobyzau.tg.bot.pbot.bots.game.election.ElectionFinalizer;
import by.kobyzau.tg.bot.pbot.checker.BotActionChecker;
import by.kobyzau.tg.bot.pbot.checker.BotTypeBotActionChecker;
import by.kobyzau.tg.bot.pbot.checker.TextBotActionChecker;
import by.kobyzau.tg.bot.pbot.collectors.CollectionBotActionCollector;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.VoteInlineMessageDto;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.ElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ElectionUpdateHandlerTest {

  @Rule public TestName testName = new TestName();

  private final long chatId = 123;
  private final int callerId = 1;
  private final int targetId = 2;
  private User bot;

  @InjectMocks private ElectionUpdateHandler handler;

  @Mock private ElectionService electionService;
  @Spy private CollectionBotActionCollector botActionCollector = new CollectionBotActionCollector();
  @Mock private PidorService pidorService;
  @Mock private DailyPidorRepository dailyPidorRepository;
  @Mock private ElectionFinalizer electionFinalizer;
  @Mock private ElectionStatPrinter electionStatPrinter;
  @Spy private Executor executor = new RuntimeExecutor();

  @Before
  public void init() {
    ReflectionTestUtils.setField(handler, "botUserName", "bot");
    bot = new User(0, "bot", true);
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
    System.out.println("************ START " + testName.getMethodName() + " *************");
  }

  @After
  public void after() {
    System.out.println("************ END *************");
  }

  @Test
  public void test_notToday() {
    // given
    LocalDate date = LocalDate.of(2020, 10, 15);
    doReturn(false).when(electionService).isElectionDay(date);

    // when
    boolean result = handler.test(date);

    // then
    assertFalse(result);
  }

  @Test
  public void test_today() {
    // given
    LocalDate date = LocalDate.of(2020, 10, 15);
    doReturn(true).when(electionService).isElectionDay(date);

    // when
    boolean result = handler.test(date);

    // then
    assertTrue(result);
  }

  @Test
  public void handleUpdate_noCallback() {
    // given
    setupPidorOfTheDay();
    Update update = new Update();

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(Collections.emptyList());
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
    checkActions(Collections.emptyList());
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
    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageDto("id", targetId, callerId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(Collections.emptyList());
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
    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageDto("id", targetId, callerId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(
        Collections.singletonList(new BotTypeBotActionChecker(EditMessageBotAction.class)));
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
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageDto("id", targetId, callerId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(Optional.empty()).when(pidorService).getPidor(chatId, targetId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(
        Collections.singletonList(new BotTypeBotActionChecker(EditMessageBotAction.class)));
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
    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageDto("id", targetId, callerId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(Optional.empty()).when(pidorService).getPidor(chatId, callerId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(
        Collections.singletonList(new BotTypeBotActionChecker(EditMessageBotAction.class)));
    assertNotFinalized();
    assertNotPrinter();
    assertNotSavedVote();
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
    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageDto("id", targetId, callerId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);
    doReturn(5).when(electionService).getNumToVote(chatId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(
        Arrays.asList(
            new BotTypeBotActionChecker(EditMessageBotAction.class),
            new TextBotActionChecker(chatId, new SimpleText("Твой голос засчитан"))));
    assertNotFinalized();
    assertPrinted();
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
    callbackQuery.setFrom(calledUser);
    callbackQuery.setData(StringUtil.serialize(new VoteInlineMessageDto("id", targetId, callerId)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    checkActions(
        Arrays.asList(
            new BotTypeBotActionChecker(EditMessageBotAction.class),
            new TextBotActionChecker(chatId, new SimpleText("Твой голос засчитан"))));
    assertFinalized();
    assertNotPrinter();
    assertSavedVote();
    assertTrue(result);
  }

  private void checkActions(List<BotActionChecker> checkerList) {
    for (int i = 0; i < checkerList.size(); i++) {
      if (i >= botActionCollector.getActionList().size()) {
        Assert.fail("No " + (i + 1) + " bot action");
      }
      checkerList.get(i).check(botActionCollector.getActionList().get(i));
    }
    assertEquals(
        "Invalid bot action count", checkerList.size(), botActionCollector.getActionList().size());
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

  private void assertPrinted() {
    verify(electionStatPrinter, times(1)).printInfo(chatId, true);
  }

  private void assertNotPrinter() {
    verify(electionStatPrinter, times(0)).printInfo(chatId, true);
  }

  private void assertSavedVote() {
    verify(electionService, times(1)).saveVote(chatId, callerId, targetId);
  }

  private void assertNotSavedVote() {
    verify(electionService, times(0)).saveVote(chatId, callerId, targetId);
  }
}
