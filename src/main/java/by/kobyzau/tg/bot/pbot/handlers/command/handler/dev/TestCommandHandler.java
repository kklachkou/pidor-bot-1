package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.model.dto.AlertDto;
import by.kobyzau.tg.bot.pbot.tg.action.SimpleBotAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.util.Collections;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class TestCommandHandler implements CommandHandler {

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    botActionCollector.add(
        new SimpleBotAction<>(
            message.getChatId(),
            SendMessage.builder()
                .text("Test 1")
                .chatId(message.getChatId().toString())
                .replyMarkup(
                    InlineKeyboardMarkup.builder()
                        .keyboardRow(
                            Collections.singletonList(
                                InlineKeyboardButton.builder()
                                    .text("Alert")
                                    .callbackData(
                                        StringUtil.serialize(new AlertDto("Alert", true, null)))
                                    .build()))
                        .keyboardRow(
                            Collections.singletonList(
                                InlineKeyboardButton.builder()
                                    .text("Alert Cached")
                                    .callbackData(
                                        StringUtil.serialize(
                                            new AlertDto(
                                                "Alert " + (new Random().nextInt() % 100),
                                                true,
                                                10)))
                                    .build()))
                        .keyboardRow(
                            Collections.singletonList(
                                InlineKeyboardButton.builder()
                                    .text("Simple Alert")
                                    .callbackData(
                                        StringUtil.serialize(new AlertDto("Alert", false, null)))
                                    .build()))
                        .keyboardRow(
                            Collections.singletonList(
                                InlineKeyboardButton.builder()
                                    .text("Simple Alert cached")
                                    .callbackData(
                                        StringUtil.serialize(
                                            new AlertDto(
                                                "Alert " + (new Random().nextInt() % 100),
                                                false,
                                                10)))
                                    .build()))
                        .build())
                .build()));
  }

  @Override
  public Command getCommand() {
    return Command.TEST;
  }
}
