package by.kobyzau.tg.bot.pbot.handlers.command.handler.dev;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.handler.CommandHandler;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class FullInfoCommandHandler implements CommandHandler {

  @Autowired private BotActionCollector botActionCollector;
  @Autowired private TelegramService telegramService;
  @Autowired private PidorService pidorService;
  @Autowired private BotService botService;
  @Autowired private DailyPidorRepository dailyPidorRepository;

  @Override
  public Command getCommand() {
    return Command.FULL_INFO;
  }

  @Override
  public void processCommand(Message message, String text) {
    List<Long> chatIds = telegramService.getChatIds();
    botActionCollector.text(
        message.getChatId(), new ParametizedText("{0} чатов", new IntText(chatIds.size())));
    for (long chatId : chatIds) {
      printChatInfo(message.getChatId(), chatId);
    }
  }

  private void printChatInfo(long sendToChatId, long chatId) {
    TextBuilder tx = new TextBuilder();
    tx.append(new SimpleText("Chat ID " + chatId))
        .append(new SimpleText(", Last pidor: "))
        .append(getLastPidorDate(chatId))
        .append(new NewLineText());
    Optional<Chat> chat = telegramService.getChat(chatId);
    chat.ifPresent(
        c ->
            tx.append(new SimpleText("Chat " + chatId))
                .append(new SimpleText(" "))
                .append(getChatName(c))
                .append(new NewLineText())
                .append(new SimpleText("CanPin: " + botService.canPinMessage(chatId)))
                .append(new NewLineText()));

    List<Pidor> pidors = pidorService.getByChat(chatId);
    tx.append(new ParametizedText("{0} registered", new IntText(pidors.size())))
        .append(new NewLineText());
    telegramService
        .getChatMemberCount(chatId)
        .ifPresent(
            count ->
                tx.append(new ParametizedText("{0} total", new IntText(count)))
                    .append(new NewLineText()));

    for (Pidor pidor : pidors) {
      tx.append(new IntText(pidor.getTgId()))
          .append(new SimpleText(": "))
          .append(new FullNamePidorText(pidor))
          .append(new SimpleText(" "))
          .append(new DateText(pidor.getUsernameLastUpdated()))
          .append(new NewLineText());
      telegramService
          .getChatMember(chatId, pidor.getTgId())
          .map(ChatMember::getStatus)
          .map(SimpleText::new)
          .ifPresent(s -> tx.append(s).append(new NewLineText()));
    }
    botActionCollector.wait(sendToChatId, 1, ChatAction.TYPING);
    botActionCollector.text(sendToChatId, tx);
  }

  private Text getChatName(Chat chat) {
    return Optional.of(chat)
        .map(Chat::getTitle)
        .filter(StringUtil::isNotBlank)
        .map(TGUtil::escapeHTML)
        .map(SimpleText::new)
        .orElse(new SimpleText("..."));
  }

  private Text getLastPidorDate(long chatId) {
    LocalDate today = DateUtil.now();
    if (dailyPidorRepository.getByChatAndDate(chatId, today).isPresent()) {
      return new SimpleText("Today");
    }
    if (dailyPidorRepository.getByChatAndDate(chatId, today.minusDays(1)).isPresent()) {
      return new SimpleText("Yesterday");
    }
    return new SimpleText("Long time ago");
  }
}
