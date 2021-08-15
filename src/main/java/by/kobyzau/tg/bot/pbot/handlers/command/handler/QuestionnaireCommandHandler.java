package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.model.QuestionnaireType;
import by.kobyzau.tg.bot.pbot.model.dto.QuestionnaireInlineDto;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.action.SimpleBotAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class QuestionnaireCommandHandler implements CommandHandler {

  @Autowired private TelegramService telegramService;
  @Autowired BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    QuestionnaireType type = QuestionnaireType.valueOf(text);

    telegramService.getChatIds().forEach(chatId -> sendQuestionnaire(chatId, type));
  }

  private void sendQuestionnaire(long chatId, QuestionnaireType type) {
    List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
    List<String> options = type.getOptions();
    for (int i = 0; i < options.size(); i++) {
      String option = options.get(i);
      buttons.add(
          Collections.singletonList(
              InlineKeyboardButton.builder()
                  .text(option)
                  .callbackData(StringUtil.serialize(new QuestionnaireInlineDto(type, i)))
                  .build()));
    }
    botActionCollector.add(
        new SimpleBotAction<>(
            SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(buildMessage(type))
                .parseMode("html")
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build()));
  }

  private String buildMessage(QuestionnaireType type) {
    return new TextBuilder(
            new ParametizedText("<pre><u>{0}</u></pre>", new SimpleText(type.getName())))
        .append(new NewLineText())
        .append(new NewLineText())
        .append(new SimpleText(type.getQuestion()))
        .text();
  }

  @Override
  public Command getCommand() {
    return Command.QUESTIONNAIRE;
  }
}
