package by.kobyzau.tg.bot.pbot.program.checker;

import by.kobyzau.tg.bot.pbot.model.ChatSetting;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.repository.chat.ChatSettingRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MultiChatSettingSystemCheckerTest {

  @Mock private ChatSettingRepository chatSettingRepository;
  @Mock private Logger logger;

  private SystemChecker checker;

  @Before
  public void init() {
    checker = new MultiChatSettingSystemChecker(chatSettingRepository, logger);
  }

  @Test
  public void check_noDuplicates() {
    // given
    doReturn(
            Arrays.asList(
                ChatSetting.builder().id(1).chatId(1).build(),
                ChatSetting.builder().id(2).chatId(3).build(),
                ChatSetting.builder().id(3).chatId(10).build()))
        .when(chatSettingRepository)
        .getAll();

    // when
    checker.check();

    // then
    verify(logger, times(0)).warn(anyString());
  }

  @Test
  public void check_hasDuplicate() {
    // given
    doReturn(
            Arrays.asList(
                ChatSetting.builder().id(1).chatId(10).build(),
                ChatSetting.builder().id(2).chatId(20).build(),
                ChatSetting.builder().id(3).chatId(20).build(),
                ChatSetting.builder().id(4).chatId(40).build()))
        .when(chatSettingRepository)
        .getAll();

    // when
    checker.check();

    // then
    verify(logger).warn("! #CHECKER\nChat has multiple settings: 20");
  }

  @Test
  public void check_hasDuplicates() {
    // given
    doReturn(
            Arrays.asList(
                ChatSetting.builder().id(1).chatId(10).build(),
                ChatSetting.builder().id(2).chatId(20).build(),
                ChatSetting.builder().id(3).chatId(20).build(),
                ChatSetting.builder().id(4).chatId(40).build(),
                ChatSetting.builder().id(5).chatId(30).build(),
                ChatSetting.builder().id(6).chatId(40).build()))
        .when(chatSettingRepository)
        .getAll();

    // when
    checker.check();

    // then
    verify(logger).warn("! #CHECKER\nChat has multiple settings: 20");
    verify(logger).warn("! #CHECKER\nChat has multiple settings: 40");
  }
}
