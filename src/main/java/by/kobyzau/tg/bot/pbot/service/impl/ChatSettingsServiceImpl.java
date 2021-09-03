package by.kobyzau.tg.bot.pbot.service.impl;

import by.kobyzau.tg.bot.pbot.model.ChatSetting;
import by.kobyzau.tg.bot.pbot.model.dto.ChatCheckboxSettingCommandDto;
import by.kobyzau.tg.bot.pbot.model.type.GameFrequent;
import by.kobyzau.tg.bot.pbot.repository.chat.ChatSettingRepository;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.service.FutureActionService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static by.kobyzau.tg.bot.pbot.service.FutureActionService.FutureActionType.ENABLE_SETTING;

@Service
public class ChatSettingsServiceImpl implements ChatSettingsService {

  @Autowired private ChatSettingRepository chatSettingRepository;
  @Autowired private FutureActionService futureActionService;

  @Override
  public boolean isEnabled(ChatCheckboxSettingType type, long chatId) {
    Optional<ChatSetting> settings = chatSettingRepository.getByChatId(chatId);
    if (!settings.isPresent()) {
      return type.getDefaultValue();
    }
    switch (type) {
      case ELECTION_HIDDEN:
        return settings.get().isElectionAnonymous();
      case GAMES_FREQUENT:
        return settings.get().getEmojiGameFrequent() == GameFrequent.FREQUENT;
      case AUTO_REGISTER_USERS:
        return settings.get().isAutoRegisterUsers();
      case ELECTION_FREQUENT:
        return settings.get().getElectionFrequent() == GameFrequent.FREQUENT;
      default:
        return type.getDefaultValue();
    }
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
    ChatSetting chatSetting =
        chatSettingRepository.getByChatId(chatId).orElseGet(() -> creteNewSetting(chatId));
    switch (type) {
      case ELECTION_HIDDEN:
        chatSetting.setElectionAnonymous(enabled);
        break;
      case GAMES_FREQUENT:
        chatSetting.setEmojiGameFrequent(enabled ? GameFrequent.FREQUENT : GameFrequent.RARE);
        break;
      case AUTO_REGISTER_USERS:
        chatSetting.setAutoRegisterUsers(enabled);
        break;
      case ELECTION_FREQUENT:
        chatSetting.setElectionFrequent(enabled ? GameFrequent.FREQUENT : GameFrequent.RARE);
        break;
    }
    if (chatSetting.getId() == 0) {
      chatSettingRepository.create(chatSetting);
    } else {
      chatSettingRepository.update(chatSetting);
    }
  }

  private ChatSetting creteNewSetting(long chatId) {
    ChatSetting.ChatSettingBuilder builder =
        ChatSetting.builder().chatId(chatId).created(DateUtil.now());
    for (ChatCheckboxSettingType type : ChatCheckboxSettingType.values()) {
      switch (type) {
        case ELECTION_HIDDEN:
          builder.electionAnonymous(type.getDefaultValue());
          break;
        case GAMES_FREQUENT:
          builder.emojiGameFrequent(
              type.getDefaultValue() ? GameFrequent.FREQUENT : GameFrequent.RARE);
          break;
        case AUTO_REGISTER_USERS:
          builder.autoRegisterUsers(type.getDefaultValue());
          break;
        case ELECTION_FREQUENT:
          builder.electionFrequent(
              type.getDefaultValue() ? GameFrequent.FREQUENT : GameFrequent.RARE);
          break;
      }
    }
    return builder.build();
  }
}
