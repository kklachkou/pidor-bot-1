package by.kobyzau.tg.bot.pbot.handlers.update;

import by.kobyzau.tg.bot.pbot.checker.BotActionAbstractTest;
import by.kobyzau.tg.bot.pbot.checker.BotTypeBotActionChecker;
import by.kobyzau.tg.bot.pbot.model.dto.CloseInlineMessageInlineDto;
import by.kobyzau.tg.bot.pbot.tg.action.EditMessageBotAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.UUID;

import static by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType.CLOSE_INLINE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CloseInlineMessageUpdateHandlerTest extends BotActionAbstractTest {

  private final long chatId = 123;
  private User bot;
  private final String id = UUID.randomUUID().toString().substring(CLOSE_INLINE.getIdSize());

  @InjectMocks private CloseInlineMessageUpdateHandler handler;

  @Before
  public void init() {
    ReflectionTestUtils.setField(handler, "botUserName", "bot");
    bot = new User(0, "bot", true);
    bot.setUserName("bot");
  }

  @Test
  public void handle_noCallback() {
    // given
    Update update = new Update();

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertFalse(processed);
    checkNoAnyActions();
  }

  @Test
  public void handle_noPrevMessage() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();

    callbackQuery.setData(StringUtil.serialize(new CloseInlineMessageInlineDto(id)));
    update.setCallbackQuery(callbackQuery);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertFalse(processed);
    checkNoAnyActions();
  }

  @Test
  public void handle_noData() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);

    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertFalse(processed);
    checkNoAnyActions();
  }

  @Test
  public void handle_anotherIndex() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);

    CloseInlineMessageInlineDto dto = new CloseInlineMessageInlineDto(id);
    dto.setIndex(-1);
    callbackQuery.setData(StringUtil.serialize(dto));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertFalse(processed);
    checkNoAnyActions();
  }

  @Test
  public void handle_editMessage() {
    // given
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    Message prevMessage = new Message();
    prevMessage.setChat(new Chat(chatId, "group"));
    prevMessage.setFrom(bot);

    callbackQuery.setData(StringUtil.serialize(new CloseInlineMessageInlineDto(id)));
    callbackQuery.setMessage(prevMessage);
    update.setCallbackQuery(callbackQuery);

    // when
    boolean processed = handler.handleUpdate(update);

    // then
    assertTrue(processed);
    checkActions(new BotTypeBotActionChecker(EditMessageBotAction.class));
  }
}
