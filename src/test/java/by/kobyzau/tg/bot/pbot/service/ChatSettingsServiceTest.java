package by.kobyzau.tg.bot.pbot.service;

import by.kobyzau.tg.bot.pbot.model.CustomDailyUserData;
import by.kobyzau.tg.bot.pbot.repository.custom.CustomDailyDataRepository;
import by.kobyzau.tg.bot.pbot.repository.custom.HashMapDailyDataRepositoryCustom;
import by.kobyzau.tg.bot.pbot.service.impl.ChatSettingsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;

import static by.kobyzau.tg.bot.pbot.service.ChatSettingsService.ChatCheckboxSettingType.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ChatSettingsServiceTest {

  @Spy private final CustomDailyDataRepository repository = new HashMapDailyDataRepositoryCustom();
  @InjectMocks private final ChatSettingsService settingsService = new ChatSettingsServiceImpl();

  private final long chatId = 123;

  @Before
  public void init() {
    repository.getAll().stream().map(CustomDailyUserData::getId).forEach(repository::delete);
  }

  @Test
  public void isEnabled_noValue_defaultTrue() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = GDPR_MESSAGE_ENABLED;
    Arrays.asList(
            getDisabledItem(GAMES_FREQUENT),
            getDisabledItem(ELECTION_HIDDEN),
            getAnotherTypeItem(type),
            getEnabledItem(GAMES_FREQUENT),
            getEnabledItem(ELECTION_HIDDEN),
            getUnknownItem())
        .forEach(repository::create);

    // when
    boolean isEnabled = settingsService.isEnabled(type, chatId);

    // then
    assertTrue(isEnabled);
  }

  @Test
  public void isEnabled_noValue_defaultFalse() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    Arrays.asList(
            getDisabledItem(GAMES_FREQUENT),
            getDisabledItem(GDPR_MESSAGE_ENABLED),
            getAnotherTypeItem(type),
            getEnabledItem(GAMES_FREQUENT),
            getEnabledItem(GDPR_MESSAGE_ENABLED),
            getUnknownItem())
        .forEach(repository::create);

    // when
    boolean isEnabled = settingsService.isEnabled(type, chatId);

    // then
    assertFalse(isEnabled);
  }

  @Test
  public void isEnabled_disabled_defaultTrue() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = GDPR_MESSAGE_ENABLED;
    Arrays.asList(
            getDisabledItem(type),
            getDisabledItem(GAMES_FREQUENT),
            getDisabledItem(ELECTION_HIDDEN),
            getAnotherTypeItem(type),
            getEnabledItem(GAMES_FREQUENT),
            getEnabledItem(ELECTION_HIDDEN),
            getUnknownItem())
        .forEach(repository::create);

    // when
    boolean isEnabled = settingsService.isEnabled(type, chatId);

    // then
    assertFalse(isEnabled);
  }

  @Test
  public void isEnabled_disabled_defaultFalse() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    Arrays.asList(
            getDisabledItem(type),
            getDisabledItem(GAMES_FREQUENT),
            getDisabledItem(GDPR_MESSAGE_ENABLED),
            getAnotherTypeItem(type),
            getEnabledItem(GAMES_FREQUENT),
            getEnabledItem(GDPR_MESSAGE_ENABLED),
            getUnknownItem())
        .forEach(repository::create);

    // when
    boolean isEnabled = settingsService.isEnabled(type, chatId);

    // then
    assertFalse(isEnabled);
  }

  @Test
  public void isEnabled_enabled_defaultTrue() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = GDPR_MESSAGE_ENABLED;
    Arrays.asList(
            getEnabledItem(type),
            getDisabledItem(GAMES_FREQUENT),
            getDisabledItem(ELECTION_HIDDEN),
            getAnotherTypeItem(type),
            getEnabledItem(GAMES_FREQUENT),
            getEnabledItem(ELECTION_HIDDEN),
            getUnknownItem())
        .forEach(repository::create);

    // when
    boolean isEnabled = settingsService.isEnabled(type, chatId);

    // then
    assertTrue(isEnabled);
  }

  @Test
  public void isEnabled_enabled_defaultFalse() {
    // given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    Arrays.asList(
            getEnabledItem(type),
            getDisabledItem(GAMES_FREQUENT),
            getDisabledItem(GDPR_MESSAGE_ENABLED),
            getAnotherTypeItem(type),
            getEnabledItem(GAMES_FREQUENT),
            getEnabledItem(GDPR_MESSAGE_ENABLED),
            getUnknownItem())
        .forEach(repository::create);

    // when
    boolean isEnabled = settingsService.isEnabled(type, chatId);

    // then
    assertTrue(isEnabled);
  }

  @Test
  public void setEnabled_noValues() {
    //given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;

    //when
    settingsService.setEnabled(type, chatId, true);

    //then
    assertTrue(settingsService.isEnabled(type, chatId));
  }

  @Test
  public void setEnabled_hasDisabled() {
    //given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    repository.create(getDisabledItem(type));

    //when
    settingsService.setEnabled(type, chatId, true);

    //then
    assertTrue(settingsService.isEnabled(type, chatId));
  }

  @Test
  public void setEnabled_hasEnabled() {
    //given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    repository.create(getEnabledItem(type));

    //when
    settingsService.setEnabled(type, chatId, true);

    //then
    assertTrue(settingsService.isEnabled(type, chatId));
  }












  @Test
  public void setDisabled_noValues() {
    //given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;

    //when
    settingsService.setEnabled(type, chatId, false);

    //then
    assertFalse(settingsService.isEnabled(type, chatId));
  }

  @Test
  public void setDisabled_hasDisabled() {
    //given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    repository.create(getDisabledItem(type));

    //when
    settingsService.setEnabled(type, chatId, false);

    //then
    assertFalse(settingsService.isEnabled(type, chatId));
  }

  @Test
  public void setDisabled_hasEnabled() {
    //given
    ChatSettingsService.ChatCheckboxSettingType type = ELECTION_HIDDEN;
    repository.create(getEnabledItem(type));

    //when
    settingsService.setEnabled(type, chatId, false);

    //then
    assertFalse(settingsService.isEnabled(type, chatId));
  }


  private CustomDailyUserData getDisabledItem(ChatSettingsService.ChatCheckboxSettingType type) {
    CustomDailyUserData customData = getBase();
    customData.setType(CustomDailyUserData.Type.CHAT_CHECKBOX_SETTING);
    customData.setData(type.name() + ":DISABLED");
    return customData;
  }

  private CustomDailyUserData getEnabledItem(ChatSettingsService.ChatCheckboxSettingType type) {
    CustomDailyUserData customData = getBase();
    customData.setType(CustomDailyUserData.Type.CHAT_CHECKBOX_SETTING);
    customData.setData(type.name());
    return customData;
  }

  private CustomDailyUserData getUnknownItem() {
    CustomDailyUserData customData = getBase();
    customData.setType(CustomDailyUserData.Type.CHAT_CHECKBOX_SETTING);
    customData.setData("UNKNOWN");
    return customData;
  }

  private CustomDailyUserData getAnotherTypeItem(ChatSettingsService.ChatCheckboxSettingType type) {
    CustomDailyUserData customData = getBase();
    customData.setType(CustomDailyUserData.Type.FUTURE_ACTION);
    customData.setData(type.name());
    return customData;
  }

  private CustomDailyUserData getBase() {
    CustomDailyUserData customData = new CustomDailyUserData();
    customData.setChatId(chatId);
    customData.setLocalDate(LocalDate.now());
    return customData;
  }
}
