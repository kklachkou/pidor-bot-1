package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.model.dto.CloseInlineMessageInlineDto;
import by.kobyzau.tg.bot.pbot.model.dto.StatInlineDto;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
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

import static by.kobyzau.tg.bot.pbot.model.dto.SerializableInlineType.STAT;

@Component
public class StatCommandHandler implements CommandHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    InlineKeyboardMarkup.InlineKeyboardMarkupBuilder keyboardMarkupBuilder =
        InlineKeyboardMarkup.builder();
    String requestId = UUID.randomUUID().toString().substring(STAT.getIdSize());

    List<InlineKeyboardButton> buttons =
        Arrays.stream(StatType.values())
            .map(
                t ->
                    InlineKeyboardButton.builder()
                        .text(t.name())
                        .callbackData(StringUtil.serialize(new StatInlineDto(requestId, t)))
                        .build())
            .collect(Collectors.toList());
    buttons.add(
        InlineKeyboardButton.builder()
            .text("Закрыть")
            .callbackData(StringUtil.serialize(new CloseInlineMessageInlineDto(requestId)))
            .build());

    keyboardMarkupBuilder.keyboard(
        buttons.stream().map(Collections::singletonList).collect(Collectors.toList()));

    botActionCollector.add(
        new ReplyKeyboardBotAction(
            chatId, new SimpleText("Статистика"), keyboardMarkupBuilder.build(), null));
  }

  @Override
  public Command getCommand() {
    return Command.STAT;
  }
}
