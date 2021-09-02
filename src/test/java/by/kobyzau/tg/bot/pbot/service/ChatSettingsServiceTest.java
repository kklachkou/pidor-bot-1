package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.ChatSetting;
import by.kobyzau.tg.bot.pbot.model.type.GameFrequent;
import by.kobyzau.tg.bot.pbot.repository.chat.ChatSettingRepository;
import by.kobyzau.tg.bot.pbot.service.impl.ChatSettingsServiceImpl;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static by.kobyzau.tg.bot.pbot.service.ChatSettingsService.ChatCheckboxSettingType.AUTO_REGISTER_USERS;
import static by.kobyzau.tg.bot.pbot.service.ChatSettingsService.ChatCheckboxSettingType.ELECTION_HIDDEN;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChatSettingsServiceTest {

  @Mock private ChatSettingRepository chatSettingRepository;
  @InjectMocks private ChatSettingsService settingsService = new ChatSettingsServiceImpl();

  private final long chatId = 123;
  private static final long SETTING_ID = 1;

  @Test
  public void isEnabled_noValue_defaultTrue() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = AUTO_REGISTER_USERS;
    doReturn(Optional.empty()).when(chatSettingRepository).getByChatId(chatId);

    // when
    boolean isEnabled = settingsService.isEnabled(type, chatId);

    // then
    assertTrue(isEnabled);
  }

  @Test
  public void isEnabled_noValue_defaultFalse() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    doReturn(Optional.empty()).when(chatSettingRepository).getByChatId(chatId);

    // when
    boolean isEnabled = settingsService.isEnabled(type, chatId);

    // then
    assertFalse(isEnabled);
  }

  @Test
  public void isEnabled_disabled_defaultTrue() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = AUTO_REGISTER_USERS;
    doReturn(Optional.of(ChatSetting.builder().id(SETTING_ID).autoRegisterUsers(false).build()))
        .when(chatSettingRepository)
        .getByChatId(chatId);

    // when
    boolean isEnabled = settingsService.isEnabled(type, chatId);

    // then
    assertFalse(isEnabled);
  }

  @Test
  public void isEnabled_disabled_defaultFalse() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    doReturn(Optional.of(ChatSetting.builder().id(SETTING_ID).electionAnonymous(false).build()))
        .when(chatSettingRepository)
        .getByChatId(chatId);

    // when
    boolean isEnabled = settingsService.isEnabled(type, chatId);

    // then
    assertFalse(isEnabled);
  }

  @Test
  public void isEnabled_enabled_defaultTrue() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = AUTO_REGISTER_USERS;
    doReturn(Optional.of(ChatSetting.builder().id(SETTING_ID).autoRegisterUsers(true).build()))
        .when(chatSettingRepository)
        .getByChatId(chatId);

    // when
    boolean isEnabled = settingsService.isEnabled(type, chatId);

    // then
    assertTrue(isEnabled);
  }

  @Test
  public void isEnabled_enabled_defaultFalse() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    doReturn(Optional.of(ChatSetting.builder().id(SETTING_ID).electionAnonymous(true).build()))
        .when(chatSettingRepository)
        .getByChatId(chatId);

    // when
    boolean isEnabled = settingsService.isEnabled(type, chatId);

    // then
    assertTrue(isEnabled);
  }

  @Test
  public void setEnabled_noValues() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    doReturn(Optional.empty()).when(chatSettingRepository).getByChatId(chatId);

    // when
    settingsService.setEnabled(type, chatId, true);

    // then
    verify(chatSettingRepository)
        .create(
            ChatSetting.builder()
                .chatId(chatId)
                .created(DateUtil.now())
                .autoRegisterUsers(true)
                .electionAnonymous(true)
                .electionFrequent(GameFrequent.FREQUENT)
                .emojiGameFrequent(GameFrequent.FREQUENT)
                .build());
  }

  @Test
  public void setEnabled_hasDisabled() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    doReturn(Optional.of(ChatSetting.builder().id(SETTING_ID).electionAnonymous(false).build()))
        .when(chatSettingRepository)
        .getByChatId(chatId);

    // when
    settingsService.setEnabled(type, chatId, true);

    // then
    verify(chatSettingRepository)
        .update(ChatSetting.builder().id(SETTING_ID).electionAnonymous(true).build());
  }

  @Test
  public void setEnabled_hasEnabled() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    doReturn(Optional.of(ChatSetting.builder().id(SETTING_ID).electionAnonymous(true).build()))
        .when(chatSettingRepository)
        .getByChatId(chatId);

    // when
    settingsService.setEnabled(type, chatId, true);

    // then
    verify(chatSettingRepository)
        .update(ChatSetting.builder().id(SETTING_ID).electionAnonymous(true).build());
  }

  @Test
  public void setDisabled_noValues() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    doReturn(Optional.empty()).when(chatSettingRepository).getByChatId(chatId);

    // when
    settingsService.setEnabled(type, chatId, false);

    // then
    verify(chatSettingRepository)
        .create(
            ChatSetting.builder()
                .chatId(chatId)
                .created(DateUtil.now())
                .autoRegisterUsers(true)
                .electionAnonymous(false)
                .electionFrequent(GameFrequent.FREQUENT)
                .emojiGameFrequent(GameFrequent.FREQUENT)
                .build());
  }

  @Test
  public void setDisabled_hasDisabled() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    doReturn(Optional.of(ChatSetting.builder().id(SETTING_ID).electionAnonymous(false).build()))
        .when(chatSettingRepository)
        .getByChatId(chatId);

    // when
    settingsService.setEnabled(type, chatId, false);

    // then
    verify(chatSettingRepository)
        .update(ChatSetting.builder().id(SETTING_ID).electionAnonymous(false).build());
  }

  @Test
  public void setDisabled_hasEnabled() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    doReturn(Optional.of(ChatSetting.builder().id(SETTING_ID).electionAnonymous(true).build()))
        .when(chatSettingRepository)
        .getByChatId(chatId);

    // when
    settingsService.setEnabled(type, chatId, false);

    // then
    verify(chatSettingRepository)
        .update(ChatSetting.builder().id(SETTING_ID).electionAnonymous(false).build());
  }
}
