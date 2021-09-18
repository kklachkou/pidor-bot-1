package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.RuntimeExecutor;
import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.entity.UserArtifact;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.bots.game.EmojiGame;
import by.kobyzau.tg.bot.pbot.bots.game.dice.DiceFinalizer;
import by.kobyzau.tg.bot.pbot.checker.*;
import by.kobyzau.tg.bot.pbot.handlers.update.impl.game.DiceDayUpdateHandler;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.PidorDice;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.DiceService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.SendStickerBotAction;
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
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DiceDayUpdateHandlerTest extends BotActionAbstractTest {

  @Rule public TestName testName = new TestName();

  private final long chatId = 123;
  private final long userId = 54321;
  private final String diceEmoji = ":)";

  @InjectMocks private DiceDayUpdateHandler handler = new DiceDayUpdateHandler();

  @Mock private UserArtifactService userArtifactService;
  @Mock private DiceService diceService;
  @Mock private PidorService pidorService;
  @Mock private DailyPidorRepository dailyPidorRepository;
  @Mock private DiceFinalizer diceFinalizer;
  @Mock private BotService botService;
  @Spy private Executor executor = new RuntimeExecutor();

  @Mock private Pidor pidor;
  @Mock private EmojiGame emojiGame;

  @Before
  public void init() {
    System.out.println("************ START " + testName.getMethodName() + " *************");
    doReturn(Optional.of(pidor)).when(pidorService).getPidor(chatId, userId);
    doReturn(Optional.of(emojiGame)).when(diceService).getGame(chatId, DateUtil.now());
    doReturn(true).when(diceService).needToFinalize(chatId);
    doReturn(diceEmoji).when(emojiGame).getEmoji();
  }

  @After
  public void after() {
    System.out.println("************ END *************");
  }

  @Test
  public void handleUpdate_messageNotValid_noMessage() {
    // given
    Update update = new Update();
    doReturn(true).when(botService).isChatValid(new Chat(chatId, "group"));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty()).when(diceService).getUserDice(chatId, userId, DateUtil.now());

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertFinalized(false);
    assertDiceNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handleUpdate_messageNotValid_chatNotValid() {
    // given
    int newDiceValue = 4;
    Update update = new Update();
    update.setMessage(getMessage(newDiceValue, diceEmoji));
    doReturn(false).when(botService).isChatValid(new Chat(chatId, "group"));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty()).when(diceService).getUserDice(chatId, userId, DateUtil.now());

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertFinalized(false);
    assertDiceNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handleUpdate_messageNotValid_fromNonPidor() {
    // given
    int newDiceValue = 4;
    Update update = new Update();
    update.setMessage(getMessage(newDiceValue, diceEmoji));
    doReturn(true).when(botService).isChatValid(new Chat(chatId, "group"));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty()).when(diceService).getUserDice(chatId, userId, DateUtil.now());
    doReturn(Optional.empty()).when(pidorService).getPidor(chatId, userId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertFinalized(false);
    assertDiceNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handleUpdate_messageNotValid_nonDiceMessage() {
    // given
    int newDiceValue = 4;
    Update update = new Update();
    update.setMessage(getTextMessage("some text"));
    doReturn(true).when(botService).isChatValid(new Chat(chatId, "group"));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty()).when(diceService).getUserDice(chatId, userId, DateUtil.now());

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertFinalized(false);
    assertDiceNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handleUpdate_messageNotValid_anotherDice() {
    // given
    int newDiceValue = 4;
    Update update = new Update();
    update.setMessage(getMessage(newDiceValue, "anotherDice"));
    doReturn(true).when(botService).isChatValid(new Chat(chatId, "group"));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty()).when(diceService).getUserDice(chatId, userId, DateUtil.now());

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertFinalized(false);
    assertDiceNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handleUpdate_nonProcessed_hasPidorOfTheDay() {
    // given
    int newDiceValue = 4;
    Update update = new Update();
    update.setMessage(getMessage(newDiceValue, diceEmoji));
    doReturn(true).when(botService).isChatValid(new Chat(chatId, "group"));
    doReturn(Optional.of(new DailyPidor()))
        .when(dailyPidorRepository)
        .getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty()).when(diceService).getUserDice(chatId, userId, DateUtil.now());

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertFinalized(false);
    assertDiceNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handleUpdate_nonProcessed_alreadyProcessed_noArtifacts() {
    // given
    doReturn(Optional.empty())
        .when(userArtifactService)
        .getUserArtifact(chatId, userId, ArtifactType.SECOND_CHANCE, DateUtil.now());
    int newDiceValue = 4;
    Update update = new Update();
    update.setMessage(getMessage(newDiceValue, diceEmoji));
    doReturn(true).when(botService).isChatValid(new Chat(chatId, "group"));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.of(new PidorDice()))
        .when(diceService)
        .getUserDice(chatId, userId, DateUtil.now());

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertFalse(result);
    assertFinalized(false);
    assertDiceNotSaved();
    checkNoAnyActions();
  }

  @Test
  public void handleUpdate_nonProcessed_alreadyProcessed_hasArtifacts_better() {
    // given
    long userArtefactId = 234;
    doReturn(Optional.of(UserArtifact.builder().id(userArtefactId).build()))
        .when(userArtifactService)
        .getUserArtifact(chatId, userId, ArtifactType.SECOND_CHANCE, DateUtil.now());
    int oldDiceValue = 4;
    int newDiceValue = 5;
    Update update = new Update();
    update.setMessage(getMessage(newDiceValue, diceEmoji));
    doReturn(true).when(botService).isChatValid(new Chat(chatId, "group"));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.of(PidorDice.builder().value(oldDiceValue).build()))
        .when(diceService)
        .getUserDice(chatId, userId, DateUtil.now());

    // when
    boolean result = handler.handleUpdate(update);

    // then
    verify(userArtifactService).deleteArtifact(userArtefactId);
    assertTrue(result);
    assertFinalized(true);
    assertDiceSaved(newDiceValue);
    checkActions(
        new BotTypeBotActionChecker(WaitBotAction.class),
        new TextBotActionChecker(
            chatId,
            new ParametizedText(
                "Второй бросок вышел лучше. Артефакт {0} использован",
                new SimpleText(ArtifactType.SECOND_CHANCE.getName()))),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new RandomTextBotActionChecker(
            "Окей, твоё очко задействовано!",
            "Я тебя понял!",
            "Спасибо!",
            "Вижу!",
            "Неплохо!",
            "Отлично!"),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new ContainsTextBotActionChecker("Актуальный список кандидатов на пидора дня:"));
  }

  @Test
  public void handleUpdate_nonProcessed_alreadyProcessed_hasArtifacts_worse() {
    // given
    long userArtefactId = 234;
    doReturn(Optional.of(UserArtifact.builder().id(userArtefactId).build()))
        .when(userArtifactService)
        .getUserArtifact(chatId, userId, ArtifactType.SECOND_CHANCE, DateUtil.now());
    int oldDiceValue = 5;
    int newDiceValue = 4;
    Update update = new Update();
    update.setMessage(getMessage(newDiceValue, diceEmoji));
    doReturn(true).when(botService).isChatValid(new Chat(chatId, "group"));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.of(PidorDice.builder().value(oldDiceValue).build()))
        .when(diceService)
        .getUserDice(chatId, userId, DateUtil.now());

    // when
    boolean result = handler.handleUpdate(update);

    // then
    verify(userArtifactService).deleteArtifact(userArtefactId);
    assertTrue(result);
    assertFinalized(false);
    assertDiceNotSaved();
    checkActions(
        new BotTypeBotActionChecker(WaitBotAction.class),
        new TextBotActionChecker(
            chatId,
            new ParametizedText(
                "Второй бросок тебе не помог. Артефакт {0} использован",
                new SimpleText(ArtifactType.SECOND_CHANCE.getName()))));
  }

  @Test
  public void handleUpdate_processed_finalized() {
    // given
    int newDiceValue = 4;
    Update update = new Update();
    update.setMessage(getMessage(newDiceValue, diceEmoji));
    doReturn(true).when(botService).isChatValid(new Chat(chatId, "group"));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty()).when(diceService).getUserDice(chatId, userId, DateUtil.now());

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertTrue(result);
    assertFinalized(true);
    assertDiceSaved(newDiceValue);
    checkActions(
        new BotTypeBotActionChecker(WaitBotAction.class),
        new RandomTextBotActionChecker(
            "Окей, твоё очко задействовано!",
            "Я тебя понял!",
            "Спасибо!",
            "Вижу!",
            "Неплохо!",
            "Отлично!"),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new ContainsTextBotActionChecker("Актуальный список кандидатов на пидора дня:"));
  }

  @Test
  public void handleUpdate_processed_onePoint_finalized() {
    // given
    int newDiceValue = 1;
    Update update = new Update();
    update.setMessage(getMessage(newDiceValue, diceEmoji));
    doReturn(true).when(botService).isChatValid(new Chat(chatId, "group"));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty()).when(diceService).getUserDice(chatId, userId, DateUtil.now());

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertTrue(result);
    assertFinalized(true);
    assertDiceSaved(newDiceValue);
    checkActions(
        new BotTypeBotActionChecker(WaitBotAction.class),
        new RandomTextBotActionChecker(
            "Окей, твоё очко задействовано!",
            "Я тебя понял!",
            "Спасибо!",
            "Вижу!",
            "Неплохо!",
            "Отлично!"),
        new BotTypeBotActionChecker(SendStickerBotAction.class),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new ContainsTextBotActionChecker("Актуальный список кандидатов на пидора дня:"));
  }

  @Test
  public void handleUpdate_processed_notFinalized() {
    // given
    int newDiceValue = 4;
    Update update = new Update();
    update.setMessage(getMessage(newDiceValue, diceEmoji));
    doReturn(true).when(botService).isChatValid(new Chat(chatId, "group"));
    doReturn(Optional.empty()).when(dailyPidorRepository).getByChatAndDate(chatId, DateUtil.now());
    doReturn(Optional.empty()).when(diceService).getUserDice(chatId, userId, DateUtil.now());
    doReturn(false).when(diceService).needToFinalize(chatId);

    // when
    boolean result = handler.handleUpdate(update);

    // then
    assertTrue(result);
    assertFinalized(false);
    assertDiceSaved(newDiceValue);
    checkActions(
        new BotTypeBotActionChecker(WaitBotAction.class),
        new RandomTextBotActionChecker(
            "Окей, твоё очко задействовано!",
            "Я тебя понял!",
            "Спасибо!",
            "Вижу!",
            "Неплохо!",
            "Отлично!"),
        new BotTypeBotActionChecker(WaitBotAction.class),
        new ContainsTextBotActionChecker("Актуальный список кандидатов на пидора дня:"));
  }

  private Message getMessage(int diceValue, String diceEmoji) {
    Message message = new Message();
    message.setChat(new Chat(chatId, "group"));
    message.setDice(new Dice(diceValue, diceEmoji));
    message.setFrom(new User(userId, "user", false));
    return message;
  }

  private Message getTextMessage(String text) {
    Message message = new Message();
    message.setChat(new Chat(chatId, "group"));
    message.setText(text);
    message.setFrom(new User(userId, "user", false));
    return message;
  }

  private void assertFinalized(boolean finalized) {
    verify(diceFinalizer, times(finalized ? 1 : 0)).finalize(chatId);
  }

  private void assertDiceSaved(int value) {
    verify(diceService, times(1)).saveDice(new PidorDice(userId, chatId, DateUtil.now(), value));
  }

  private void assertDiceNotSaved() {
    verify(diceService, times(0)).saveDice(any());
  }
}
