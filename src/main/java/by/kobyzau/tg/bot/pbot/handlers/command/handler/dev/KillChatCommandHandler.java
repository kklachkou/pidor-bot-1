package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.dto.KillChatDto;
import by.kobyzau.tg.bot.pbot.program.text.DateText;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.action.SimpleBotAction;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KillChatCommandHandler implements CommandHandler {

  private final BotActionCollector botActionCollector;
  private final PidorService pidorService;
  private final PidorRepository pidorRepository;
  private final DailyPidorRepository dailyPidorRepository;
  private final TelegramService telegramService;

  @Override
  public void processCommand(Message message, String text) {
    long targetChatId = StringUtil.parseLong(text, 0);
    if (targetChatId == 0) {
      botActionCollector.text(message.getChatId(), new SimpleText("Неверный chat_id: " + text));
      return;
    }
    int activePidors = pidorService.getByChat(targetChatId).size();
    int savedPidors = pidorRepository.getByChat(targetChatId).size();
    List<DailyPidor> dailyPidors = dailyPidorRepository.getByChat(targetChatId);
    int gamesCount = dailyPidors.size();
    LocalDate lastGame =
        dailyPidors.stream()
            .max(Comparator.comparing(DailyPidor::getLocalDate))
            .map(DailyPidor::getLocalDate)
            .orElse(LocalDate.of(1999, 1, 1));

    String chatName =
        telegramService
            .getChat(targetChatId)
            .map(Chat::getTitle)
            .map(TGUtil::escapeHTML)
            .orElse("-no title-");
    botActionCollector.add(
        new SimpleBotAction<>(
            message.getChatId(),
            SendMessage.builder()
                .chatId(String.valueOf(message.getChatId()))
                .text(
                    new ParametizedText(
                            "Name: {0}\nSaved: {1}\nActive: {2}\nGames: {3}\nLast Game: {4}",
                            new SimpleText(chatName),
                            new IntText(savedPidors),
                            new IntText(activePidors),
                            new IntText(gamesCount),
                            new DateText(lastGame))
                        .text())
                .replyMarkup(
                    InlineKeyboardMarkup.builder()
                        .keyboardRow(
                            Collections.singletonList(
                                InlineKeyboardButton.builder()
                                    .text("Удалить")
                                    .callbackData(
                                        StringUtil.serialize(new KillChatDto(targetChatId)))
                                    .build()))
                        .build())
                .build(),
            true));
  }

  @Override
  public Command getCommand() {
    return Command.KILL_CHAT;
  }
}
