package by.kobyzau.tg.bot.pbot.handlers.update.impl.callback;

import by.kobyzau.tg.bot.pbot.artifacts.ArtifactType;
import by.kobyzau.tg.bot.pbot.artifacts.helper.BlackBoxHelper;
import by.kobyzau.tg.bot.pbot.artifacts.service.UserArtifactService;
import by.kobyzau.tg.bot.pbot.checker.BotActionAbstractTest;
import by.kobyzau.tg.bot.pbot.checker.SimpleActionChecker;
import by.kobyzau.tg.bot.pbot.checker.TextBotActionChecker;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.dto.OpenBlackBoxDto;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.program.text.ItalicText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.sender.BotSender;
import by.kobyzau.tg.bot.pbot.sender.methods.SendMethod;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.SimpleBotAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.helper.CollectionHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OpenBlackBoxUpdateHandlerTest extends BotActionAbstractTest {

  private static final long CHAT_ID = 123;
  private static final long USER_ID = 321;
  private static final int MESSAGE_ID = 987;
  private static final String CALLBACK_ID = "callbackId";
  private static final String REQUEST_ID = "requestId";
  private static final String NEW_REQUEST_ID = "newRequestId";
  @Mock private PidorService pidorService;
  @Mock private UserArtifactService userArtifactService;
  @Mock private CollectionHelper collectionHelper;
  @Mock private BotSender directPidorBotSender;
  @Mock private BlackBoxHelper blackBoxHelper;
  @InjectMocks private OpenBlackBoxUpdateHandler handler;
  private Update update;

  @Before
  public void init() {
    update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    callbackQuery.setId(CALLBACK_ID);
    Message message = new Message();
    message.setMessageId(MESSAGE_ID);
    message.setChat(new Chat(CHAT_ID, "group"));
    update.setCallbackQuery(callbackQuery);
    callbackQuery.setFrom(new User(USER_ID, "User" + USER_ID, false));
    callbackQuery.setMessage(message);
    doReturn(NEW_REQUEST_ID).when(blackBoxHelper).getRequestId();
  }

  @Test
  public void getDtoType_test() {
    // when
    Class<OpenBlackBoxDto> dtoType = handler.getDtoType();

    // then
    assertEquals(OpenBlackBoxDto.class, dtoType);
  }

  @Test
  public void getSerializableType_test() {
    // when
    SerializableInlineType serializableType = handler.getSerializableType();

    // then
    assertEquals(SerializableInlineType.OPEN_BLACK_BOX, serializableType);
  }

  @Test
  public void handleCallback_notPidor() {
    // given
    doReturn(Optional.empty()).when(pidorService).getPidor(CHAT_ID, USER_ID);

    // when
    handler.handleCallback(update, new OpenBlackBoxDto(REQUEST_ID));

    // then
    checkNoAnyActions();
    verify(userArtifactService, times(0)).addArtifact(eq(CHAT_ID), eq(USER_ID), any(), any());
    verify(directPidorBotSender)
        .send(
            CHAT_ID,
            SendMethod.method(
                AnswerCallbackQuery.builder()
                    .text("Вы не зарегистрированы в игре")
                    .showAlert(true)
                    .callbackQueryId(CALLBACK_ID)
                    .cacheTime(5)
                    .build()));
  }

  @Test
  public void handleCallback_hasPidor_alreadyHandled() {
    // given
    ArtifactType artifactType = ArtifactType.PIDOR_MAGNET;
    doReturn(artifactType).when(collectionHelper).getRandomValue(any());
    Pidor pidor = new Pidor();
    doReturn(Optional.of(pidor)).when(pidorService).getPidor(CHAT_ID, USER_ID);
    doReturn(true).when(blackBoxHelper).checkRequest(CHAT_ID, USER_ID, REQUEST_ID);

    // when
    handler.handleCallback(update, new OpenBlackBoxDto(REQUEST_ID));

    // then
    verify(userArtifactService, times(0))
        .addArtifact(eq(CHAT_ID), eq(USER_ID), eq(artifactType), any(LocalDate.class));
    verify(directPidorBotSender, times(1)).send(eq(CHAT_ID), any());
    checkNoAnyActions();
  }

  @Test
  public void handleCallback_hasPidor_hasArtifactsLeft() {
    // given
    int numPerDay = 4;
    int numHandled = 3;
    ArtifactType artifactType = ArtifactType.PIDOR_MAGNET;
    doReturn(artifactType).when(collectionHelper).getRandomValue(any());
    Pidor pidor = new Pidor();
    doReturn(Optional.of(pidor)).when(pidorService).getPidor(CHAT_ID, USER_ID);
    doReturn(false).when(blackBoxHelper).checkRequest(CHAT_ID, USER_ID, REQUEST_ID);
    doReturn(numPerDay).when(blackBoxHelper).getNumArtifactsPerDay(CHAT_ID);
    doReturn(numHandled).when(blackBoxHelper).getHandledNum(CHAT_ID);

    // when
    handler.handleCallback(update, new OpenBlackBoxDto(REQUEST_ID));

    // then
    verify(userArtifactService)
        .addArtifact(eq(CHAT_ID), eq(USER_ID), eq(artifactType), any(LocalDate.class));
    verify(directPidorBotSender, times(0)).send(anyLong(), any());
    checkActions(
        new SimpleActionChecker(
            new SimpleBotAction<>(
                CHAT_ID,
                EditMessageReplyMarkup.builder()
                    .replyMarkup(
                        InlineKeyboardMarkup.builder()
                            .keyboardRow(
                                Collections.singletonList(
                                    InlineKeyboardButton.builder()
                                        .text("Открыть подарок (1)")
                                        .callbackData(
                                            StringUtil.serialize(
                                                new OpenBlackBoxDto(NEW_REQUEST_ID)))
                                        .build()))
                            .build())
                    .chatId(String.valueOf(CHAT_ID))
                    .messageId(MESSAGE_ID)
                    .build(),
                true)),
        new TextBotActionChecker(
            CHAT_ID,
            new ParametizedText(
                "{0} взял яйца в кулак и открыл чёрный ящик", new FullNamePidorText(pidor))),
        new TextBotActionChecker(
            CHAT_ID,
            new ParametizedText(
                "А в ящике лежит {0} {1}!\n{2}",
                new ItalicText(artifactType.getName()),
                new SimpleText(artifactType.getEmoji()),
                new SimpleText(artifactType.getDesc()))));
  }

  @Test
  public void handleCallback_hasPidor_noArtifactsLeft() {
    // given
    int numPerDay = 4;
    int numHandled = 4;
    ArtifactType artifactType = ArtifactType.PIDOR_MAGNET;
    doReturn(artifactType).when(collectionHelper).getRandomValue(any());
    Pidor pidor = new Pidor();
    doReturn(Optional.of(pidor)).when(pidorService).getPidor(CHAT_ID, USER_ID);
    doReturn(false).when(blackBoxHelper).checkRequest(CHAT_ID, USER_ID, REQUEST_ID);
    doReturn(numPerDay).when(blackBoxHelper).getNumArtifactsPerDay(CHAT_ID);
    doReturn(numHandled).when(blackBoxHelper).getHandledNum(CHAT_ID);

    // when
    handler.handleCallback(update, new OpenBlackBoxDto(REQUEST_ID));

    // then
    verify(userArtifactService)
        .addArtifact(eq(CHAT_ID), eq(USER_ID), eq(artifactType), any(LocalDate.class));
    verify(directPidorBotSender, times(0)).send(anyLong(), any());
    checkActions(
        new SimpleActionChecker(
            new SimpleBotAction<>(
                CHAT_ID,
                EditMessageReplyMarkup.builder()
                    .replyMarkup(InlineKeyboardMarkup.builder().clearKeyboard().build())
                    .chatId(String.valueOf(CHAT_ID))
                    .messageId(MESSAGE_ID)
                    .build(),
                true)),
        new TextBotActionChecker(
            CHAT_ID,
            new ParametizedText(
                "{0} взял яйца в кулак и открыл чёрный ящик", new FullNamePidorText(pidor))),
        new TextBotActionChecker(
            CHAT_ID,
            new ParametizedText(
                "А в ящике лежит {0} {1}!\n{2}",
                new ItalicText(artifactType.getName()),
                new SimpleText(artifactType.getEmoji()),
                new SimpleText(artifactType.getDesc()))));
  }
}