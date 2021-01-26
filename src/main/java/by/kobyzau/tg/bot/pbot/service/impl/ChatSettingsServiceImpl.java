package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.model.CustomDailyUserData;
import by.kobyzau.tg.bot.pbot.model.dto.ChatCheckboxSettingCommandDto;
import by.kobyzau.tg.bot.pbot.repository.custom.CustomDailyDataRepository;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.service.FutureActionService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static by.kobyzau.tg.bot.pbot.model.CustomDailyUserData.Type.CHAT_CHECKBOX_SETTING;
import static by.kobyzau.tg.bot.pbot.service.FutureActionService.FutureActionType.ENABLE_SETTING;

@Service
public class ChatSettingsServiceImpl implements ChatSettingsService {

  private static final String DISABLED_KEY = ":DISABLED";

  @Autowired private CustomDailyDataRepository repository;
  @Autowired private FutureActionService futureActionService;

  @Override
  public boolean isEnabled(ChatCheckboxSettingType type, long chatId) {

    boolean isEnabled =
        repository.getByChat(chatId).stream()
            .filter(s -> s.getType() == CHAT_CHECKBOX_SETTING)
            .anyMatch(s -> Objects.equals(s.getData(), type.name()));
    if (isEnabled) {
      return true;
    }
    String disabledKey = type.name() + DISABLED_KEY;
    boolean isDisabled =
        repository.getByChat(chatId).stream()
            .filter(s -> s.getType() == CHAT_CHECKBOX_SETTING)
            .anyMatch(s -> Objects.equals(s.getData(), disabledKey));
    return !isDisabled && type.getDefaultValue();
  }

  @Override
  public boolean willBeChanged(ChatCheckboxSettingType type, long chatId) {
    if (!type.isChangedInFuture()) {
      return false;
    }
    return futureActionService
        .getFutureActionData(ENABLE_SETTING, LocalDate.of(2100, 1, 1))
        .stream()
        .map(d -> StringUtil.deserialize(d, ChatCheckboxSettingCommandDto.class))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(c -> c.getType() == type)
        .anyMatch(c -> Objects.equals(chatId, c.getChatId()));
  }

  @Override
  public void setEnabled(ChatCheckboxSettingType type, long chatId, boolean enabled) {
    if (enabled) {
      enable(chatId, type);
    } else {
      disable(chatId, type);
    }
  }

  private void enable(long chatId, ChatCheckboxSettingType type) {
    CustomDailyUserData data = new CustomDailyUserData();
    data.setType(CHAT_CHECKBOX_SETTING);
    data.setLocalDate(DateUtil.now());
    int defaultId = 0;
    data.setPlayerTgId(defaultId);
    data.setChatId(chatId);
    data.setData(type.name());
    repository.create(data);
  }

  private void disable(long chatId, ChatCheckboxSettingType type) {
    repository.getByChat(chatId).stream()
        .filter(s -> s.getType() == CHAT_CHECKBOX_SETTING)
        .filter(s -> Objects.equals(s.getData(), type.name()))
        .map(CustomDailyUserData::getId)
        .forEach(repository::delete);
    CustomDailyUserData data = new CustomDailyUserData();
    data.setType(CHAT_CHECKBOX_SETTING);
    data.setLocalDate(DateUtil.now());
    int defaultId = 0;
    data.setPlayerTgId(defaultId);
    data.setChatId(chatId);
    data.setData(type.name() + DISABLED_KEY);
    repository.create(data);
  }
}
