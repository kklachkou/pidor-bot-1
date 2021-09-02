package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.repository.custom.CustomDailyDataRepository;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Objects;

import static by.kobyzau.tg.bot.pbot.model.CustomDailyUserData.Type.CHAT_CHECKBOX_SETTING;

@Component
public class TestCommandHandler implements CommandHandler {

  @Autowired private ChatSettingsService chatSettingsService;
  @Autowired private CustomDailyDataRepository customDailyDataRepository;
  @Autowired private TelegramService telegramService;

  @Override
  public void processCommand(Message message, String text) {
    telegramService.getChatIds().forEach(this::migrateSetting);
  }

  private void migrateSetting(long chatId) {
    for (ChatSettingsService.ChatCheckboxSettingType type :
        ChatSettingsService.ChatCheckboxSettingType.values()) {
      chatSettingsService.setEnabled(type, chatId, isEnabled(chatId, type));
    }
  }

  private boolean isEnabled(long chatId, ChatSettingsService.ChatCheckboxSettingType type) {
    boolean isEnabled =
        customDailyDataRepository.getByChat(chatId).stream()
            .filter(s -> s.getType() == CHAT_CHECKBOX_SETTING)
            .anyMatch(s -> Objects.equals(s.getData(), type.name()));
    if (isEnabled) {
      return true;
    }
    String disabledKey = type.name() + ":DISABLED";
    boolean isDisabled =
        customDailyDataRepository.getByChat(chatId).stream()
            .filter(s -> s.getType() == CHAT_CHECKBOX_SETTING)
            .anyMatch(s -> Objects.equals(s.getData(), disabledKey));
    return !isDisabled && type.getDefaultValue();
  }

  @Override
  public Command getCommand() {
    return Command.TEST;
  }
}
