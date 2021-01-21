package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.model.dto.CheckboxSettingCommandInlineDto;
import by.kobyzau.tg.bot.pbot.model.dto.CloseInlineMessageInlineDto;
import by.kobyzau.tg.bot.pbot.program.text.BoldText;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.service.ChatSettingsService;
import by.kobyzau.tg.bot.pbot.tg.action.ReplyKeyboardBotAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType.SETTING_ROOT;

@Component
public class ChatSettingsCommandHandler implements CommandHandler {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private ChatSettingsService settingsService;

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    InlineKeyboardMarkup.InlineKeyboardMarkupBuilder keyboardMarkupBuilder =
        InlineKeyboardMarkup.builder();
    String requestId = UUID.randomUUID().toString().substring(SETTING_ROOT.getIdSize());

    List<InlineKeyboardButton> buttons =
        Arrays.stream(ChatSettingsService.ChatCheckboxSettingType.values())
            .map(
                t ->
                    InlineKeyboardButton.builder()
                        .text(
                            t.getName()
                                + " "
                                + (settingsService.isEnabled(t, chatId)
                                    ? t.getEnabledMark()
                                    : t.getDisabledMark()))
                        .callbackData(
                            StringUtil.serialize(new CheckboxSettingCommandInlineDto(requestId, t)))
                        .build())
            .collect(Collectors.toList());
    buttons.add(
        InlineKeyboardButton.builder()
            .text("Закрыть")
            .callbackData(StringUtil.serialize(new CloseInlineMessageInlineDto(requestId)))
            .build());

    keyboardMarkupBuilder.keyboard(
        buttons.stream().map(Collections::singletonList).collect(Collectors.toList()));
    TextBuilder description = new TextBuilder();
    description
        .append(new BoldText("Настройки чата"))
        .append(new NewLineText())
        .append(new NewLineText());

    for (ChatSettingsService.ChatCheckboxSettingType type :
        ChatSettingsService.ChatCheckboxSettingType.values()) {
      description
          .append(new BoldText(" - " + type.getName()))
          .append(new SimpleText(" - " + type.getDescription()))
          .append(new NewLineText());
    }
    botActionCollector.add(
        new ReplyKeyboardBotAction(chatId, description, keyboardMarkupBuilder.build(), null));
  }

  @Override
  public Command getCommand() {
    return Command.SETTINGS;
  }
}
