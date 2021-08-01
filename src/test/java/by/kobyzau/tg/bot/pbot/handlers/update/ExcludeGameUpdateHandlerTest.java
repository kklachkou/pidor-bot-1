package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.RuntimeExecutor;
import by.kobyzau.tg.bot.pbot.bots.game.exclude.ExcludeFinalizer;
import by.kobyzau.tg.bot.pbot.checker.BotActionAbstractTest;
import by.kobyzau.tg.bot.pbot.checker.BotTypeBotActionChecker;
import by.kobyzau.tg.bot.pbot.checker.ContainsTextBotActionChecker;
import by.kobyzau.tg.bot.pbot.handlers.update.impl.game.ExcludeGameUpdateHandler;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.ExcludeGameUserValue;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.ExcludeGameService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.ChatActionBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.WaitBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
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
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExcludeGameUpdateHandlerTest extends BotActionAbstractTest {

  @Rule public TestName testName = new TestName();

  private final long chatId = 123;
  private final long userId = 54321;
  private final String excludeWord = "not me";

  @InjectMocks private ExcludeGameUpdateHandler handler = new ExcludeGameUpdateHandler();

  @Mock private ExcludeGameService excludeGameService;
  @Mock private PidorService pidorService;
  @Mock private DailyPidorRepository dailyPidorRepository;
  @Mock private BotService botService;
  @Mock private ExcludeFinalizer excludeFinalizer;
  @Spy private Executor executor = new RuntimeExecutor();

  @Mock private Pidor pidor;
  @Mock private ExcludeGameUserValue savedValue;

  @Before
  public void init() {
    System.out.println("************ START " + testName.getMethodName() + " *************");
    doReturn(userId).when(pidor).getTgId();
    doReturn(chatId).when(pidor).getChatId();
    doReturn(Optional.of(pidor)).when(pidorService).getPidor(chatId, userId);
    doReturn(true).when(botService).isChatValid(chatId);
    doReturn(excludeWord).when(excludeGameService).getWordOfTheDay(any());
    doReturn(userId).when(savedValue).getPlayerTgId();
    doReturn(chatId).when(savedValue).getChatId();
    doReturn(DateUtil.now()).when(savedValue).getLocalDate();
    doReturn(Collections.singletonList(savedValue))
        .when(excludeGameService)
        .getExcludeGameUserValues(chatId, DateUtil.now());
    doReturn(true).when(excludeGameService).needToFinalize(chatId);
    doReturn(true).when(excludeGameService).isExcludeGameDay(chatId, DateUtil.now());
  }

  @After
  public void after() {
    System.out.println("************ END *************");
  }

  @Test
  public void handleUpdate_messageNotValid_noMessage() {
    // given
    Update update = new Update();
    doReturn(Optional.of(mock(DailyPidor.class))).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty())
            .when(excludeGameService)
            .getExcludeGameUserValue(chatId, userId, DateUtil.now());
    doReturn(Arrays.asList(pidor, mock(Pidor.class))).when(pidorService).getByChat(chatId);


    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertUserValueSaved(false);
    assertFinalized(false);
    checkNoAnyActions();
  }
 @Test
  public void handleUpdate_messageNotValid_chatIsNotValid() {
    // given
   Update update = new Update();
   update.setMessage(getMessage(excludeWord));
   doReturn(Optional.of(mock(DailyPidor.class))).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
   doReturn(Optional.empty())
           .when(excludeGameService)
           .getExcludeGameUserValue(chatId, userId, DateUtil.now());
   doReturn(Arrays.asList(pidor, mock(Pidor.class))).when(pidorService).getByChat(chatId);
   doReturn(false).when(botService).isChatValid(chatId);

   // when
    boolean result = handler.handleUpdate(update);

    // then
   assertFalse(result);
   assertUserValueSaved(false);
   assertFinalized(false);
   checkNoAnyActions();
  }

  @Test
  public void handleUpdate_hasUserValue_notFinalized() {
    // given
    Update update = new Update();
    update.setMessage(getMessage(excludeWord));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.of(savedValue))
        .when(excludeGameService)
        .getExcludeGameUserValue(chatId, userId, DateUtil.now());
    doReturn(Arrays.asList(pidor, mock(Pidor.class))).when(pidorService).getByChat(chatId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertTrue(result);
    assertUserValueSaved(false);
    assertFinalized(false);
    checkActions(
        new BotTypeBotActionChecker(ChatActionBotAction.class),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new ContainsTextBotActionChecker(", не нужно повторяться"));
  }

  @Test
  public void handleUpdate_processed_notFinalized() {
    // given
    Update update = new Update();
    update.setMessage(getMessage(excludeWord));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty())
        .when(excludeGameService)
        .getExcludeGameUserValue(chatId, userId, DateUtil.now());
    doReturn(Arrays.asList(pidor, mock(Pidor.class))).when(pidorService).getByChat(chatId);
    doReturn(false).when(excludeGameService).needToFinalize(chatId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertTrue(result);
    assertUserValueSaved(true);
    assertFinalized(false);
    checkActions(
        new BotTypeBotActionChecker(ChatActionBotAction.class),
        new ContainsTextBotActionChecker(
            "сегодня ты не будешь пидором",
            "поздравляю, сегодня ты натурал",
            "выдохни, ты успел",
            "ты пидор! Шутка, ты - красавчик",
            "я тебя понял",
            "принято",
            "ты тип странный, но сегодня ты не будешь пидором дня"),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new ContainsTextBotActionChecker("Осталось ", "Ещё ", "Ждём ещё "));
  }

  @Test
  public void handleUpdate_processed_finalized() {
    // given
    Update update = new Update();
    update.setMessage(getMessage(excludeWord));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty())
        .when(excludeGameService)
        .getExcludeGameUserValue(chatId, userId, DateUtil.now());
    doReturn(Arrays.asList(pidor, mock(Pidor.class))).when(pidorService).getByChat(chatId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertTrue(result);
    assertUserValueSaved(true);
    assertFinalized(true);
    checkActions(
        new BotTypeBotActionChecker(ChatActionBotAction.class),
        new ContainsTextBotActionChecker(
            "сегодня ты не будешь пидором",
            "поздравляю, сегодня ты натурал",
            "выдохни, ты успел",
            "ты пидор! Шутка, ты - красавчик",
            "я тебя понял",
            "принято",
            "ты тип странный, но сегодня ты не будешь пидором дня"),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new ContainsTextBotActionChecker("Кто не успел, тот пидор!"),
        new BotTypeBotActionChecker(ChatActionBotAction.class));
  }

  @Test
  public void handleUpdate_notProcessed_userNotPidor() {
    // given
    Update update = new Update();
    update.setMessage(getMessage(excludeWord));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty())
        .when(excludeGameService)
        .getExcludeGameUserValue(chatId, userId, DateUtil.now());
    doReturn(Arrays.asList(pidor, mock(Pidor.class))).when(pidorService).getByChat(chatId);
    doReturn(Optional.empty()).when(pidorService).getPidor(chatId, userId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertUserValueSaved(false);
    assertFinalized(false);
    checkNoAnyActions();
  }

  @Test
  public void handleUpdate_nonProcessed_hasPidorOfDay() {
    // given
    Update update = new Update();
    update.setMessage(getMessage(excludeWord));
    doReturn(Optional.of(mock(DailyPidor.class)))
        .when(dailyPidorRepository)
        .getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty())
        .when(excludeGameService)
        .getExcludeGameUserValue(chatId, userId, DateUtil.now());
    doReturn(Arrays.asList(pidor, mock(Pidor.class))).when(pidorService).getByChat(chatId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertUserValueSaved(false);
    assertFinalized(false);
    checkNoAnyActions();
  }

  @Test
  public void handleUpdate_nonProcessed_noExcludeWord() {
    // given
    Update update = new Update();
    update.setMessage(getMessage(excludeWord + "+"));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty())
        .when(excludeGameService)
        .getExcludeGameUserValue(chatId, userId, DateUtil.now());
    doReturn(Arrays.asList(pidor, mock(Pidor.class))).when(pidorService).getByChat(chatId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertUserValueSaved(false);
    assertFinalized(false);
    checkNoAnyActions();
  }

    @Test
    public void handleUpdate_notGameDay_notProcessed_notFinalized() {
        // given
        Update update = new Update();
        update.setMessage(getMessage(excludeWord));
        doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
        doReturn(Optional.empty())
                .when(excludeGameService)
                .getExcludeGameUserValue(chatId, userId, DateUtil.now());
        doReturn(Arrays.asList(pidor, mock(Pidor.class))).when(pidorService).getByChat(chatId);
        doReturn(false).when(excludeGameService).isExcludeGameDay(chatId, DateUtil.now());

        // when
        boolean result = handler.handleUpdate(update);

        // then
        assertFalse(result);
        assertUserValueSaved(false);
        assertFinalized(false);
        checkNoAnyActions();
    }


    private Message getMessage(String text) {
    Message message = new Message();
    message.setChat(new Chat(chatId, "group"));
    message.setText(text);
    message.setFrom(new User(userId, "user", false));
    return message;
  }

  private void assertFinalized(boolean finalized) {
    verify(excludeFinalizer, times(finalized ? 1 : 0)).finalize(chatId);
  }

  private void assertUserValueSaved(boolean saved) {
    verify(excludeGameService, times(saved ? 1 : 0)).saveExcludeGameUserValue(any());
  }
}
