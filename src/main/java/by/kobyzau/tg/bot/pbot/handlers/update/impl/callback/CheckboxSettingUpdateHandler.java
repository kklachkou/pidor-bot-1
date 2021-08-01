package by.kobyzau.tg.bot.pbot.handlers.update.impl.callback;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandlerStage;
import by.kobyzau.tg.bot.pbot.model.dto.ChatCheckboxSettingCommandDto;
import by.kobyzau.tg.bot.pbot.model.dto.CheckboxSettingCommandInlineDto;
import by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType;
import by.kobyzau.tg.bot.pbot.program.printer.SettingsCommandPrinter;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.service.FutureActionService;
import by.kobyzau.tg.bot.pbot.tg.action.AnswerCallbackBotAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;

import static by.kobyzau.tg.bot.pbot.service.FutureActionService.FutureActionType.ENABLE_SETTING;

@Component
public class CheckboxSettingUpdateHandler implements UpdateHandler {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private ChatSettingsService chatSettingsService;
  @Autowired private FutureActionService futureActionService;
  @Autowired private SettingsCommandPrinter settingsCommandPrinter;

  @Value("${bot.pidor.username}")
  private String botUserName;

  @Autowired
  @Qualifier("cachedExecutor")
  private Executor executor;

  @Override
  public UpdateHandlerStage getStage() {
    return UpdateHandlerStage.CALLBACK;
  }

  @Override
  public boolean handleUpdate(Update update) {
    CallbackQuery callbackQuery = update.getCallbackQuery();
    if (callbackQuery == null) {
      return false;
    }
    Message prevMessage = callbackQuery.getMessage();
    Optional<CheckboxSettingCommandInlineDto> data =
        StringUtil.deserialize(callbackQuery.getData(), CheckboxSettingCommandInlineDto.class);
    if (prevMessage == null
        || prevMessage.getFrom() == null
        || !data.isPresent()
        || data.get().getType() == null
        || !Objects.equals(SerializableInlineType.SETTING_ROOT.getIndex(), data.get().getIndex())
        || !botUserName.equalsIgnoreCase(prevMessage.getFrom().getUserName())) {
      return false;
    }
    executor.execute(() -> handleSetting(callbackQuery.getId(), prevMessage, data.get()));
    return true;
  }

  private void handleSetting(
      String callbackId, Message prevMessage, CheckboxSettingCommandInlineDto data) {
    long chatId = prevMessage.getChatId();
    ChatSettingsService.ChatCheckboxSettingType settingType = data.getType();
    boolean isEnabled = chatSettingsService.isEnabled(settingType, chatId);
    if (settingType.isChangedInFuture()) {
      futureActionService.saveFutureActionData(
          ENABLE_SETTING,
          LocalDate.now().plusDays(1),
          StringUtil.serialize(new ChatCheckboxSettingCommandDto(chatId, settingType, !isEnabled)));
      botActionCollector.add(
          new AnswerCallbackBotAction(
              chatId,
              callbackId,
              new SimpleText("Настройка сохранена и будет примерена в течении суток"),
              true));
    } else {
      chatSettingsService.setEnabled(settingType, chatId, !isEnabled);
      botActionCollector.add(
          new AnswerCallbackBotAction(
              chatId, callbackId, new SimpleText("Настройка сохранена и применена")));
    }
    refreshSettingMessage(prevMessage);
  }

  private void refreshSettingMessage(Message prevMessage) {
    settingsCommandPrinter.editSettingsMessage(prevMessage);
  }
}
