package by.kobyzau.tg.bot.pbot.handlers.future.impl;

import by.kobyzau.tg.bot.pbot.handlers.future.FutureActionHandler;
import by.kobyzau.tg.bot.pbot.model.dto.ChatCheckboxSettingCommandDto;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.service.FutureActionService;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EnableSettingFutureActionHandler implements FutureActionHandler {

  @Autowired private ChatSettingsService chatSettingsService;

  @Override
  public FutureActionService.FutureActionType getType() {
    return FutureActionService.FutureActionType.ENABLE_SETTING;
  }

  @Override
  public void processAction(String data) {
    Optional<ChatCheckboxSettingCommandDto> commandDto =
        StringUtil.deserialize(data, ChatCheckboxSettingCommandDto.class);
    commandDto.ifPresent(
        c -> chatSettingsService.setEnabled(c.getType(), c.getChatId(), c.getEnable()));
  }
}
