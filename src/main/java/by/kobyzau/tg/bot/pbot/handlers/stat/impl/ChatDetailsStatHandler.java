package by.kobyzau.tg.bot.pbot.handlers.stat.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.stat.StatHandler;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.BoldText;
import by.kobyzau.tg.bot.pbot.program.text.DateText;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.LongText;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;

@Component
public class ChatDetailsStatHandler implements StatHandler {
  @Autowired private TelegramService telegramService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private PidorRepository pidorRepository;
  @Autowired private Logger logger;
  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void printStat(long chatIdToSend) {

    logger.info("#DetailedStat Start");
    List<Long> chatIds =
        pidorRepository.getAll().stream()
            .map(Pidor::getChatId)
            .distinct()
            .collect(Collectors.toList());
    TextBuilder textBuilder = new TextBuilder();
    textBuilder
        .append(new IntText(chatIds.size()))
        .append(new SimpleText(" чатов."))
        .append(new NewLineText())
        .append(new NewLineText());

    logger.info("#DetailedStat Got chat count");
    for (int i = 0; i < chatIds.size(); i++) {
      logger.info("#DetailedStat " + (i + 1) + "/" + chatIds.size());
      Long chatId = chatIds.get(i);
      textBuilder
          .append(new ParametizedText("#START {0}", new LongText(chatId)))
          .append(new NewLineText());
      try {
        appendRow(textBuilder, "Имя", getChatName(chatId));
        appendRow(textBuilder, "Колличество людей", getMemberCount(chatId));
        appendRow(textBuilder, "Колличество Пидоров", getPidorCount(chatId));
        appendRow(textBuilder, "Срок", getDuration(chatId));
        appendRow(textBuilder, "Людей-контактов", getContactPeopleNum(chatId));

      } catch (Exception e) {
        logger.error("Cannot get Stat for chat " + chatId, e);
      }
      textBuilder
          .append(new ParametizedText("#END {0}", new LongText(chatId)))
          .append(new NewLineText())
          .append(new NewLineText());
    }
    logger.info("#DetailedStat Completed");
    botActionCollector.text(chatIdToSend, textBuilder);
  }

  private void appendRow(TextBuilder textBuilder, String column, Text value) {
    textBuilder
        .append(new BoldText(column))
        .append(new SimpleText(": "))
        .append(value)
        .append(new NewLineText());
  }

  private Text getPidorCount(long chatId) {
    return new IntText(pidorRepository.getByChat(chatId).size());
  }

  private Text getContactPeopleNum(long chatId) {
    List<Pidor> pidorsOfChat = pidorRepository.getByChat(chatId);
    List<Long> chatIds =
        pidorRepository.getAll().stream()
            .map(Pidor::getChatId)
            .distinct()
            .collect(Collectors.toList());
    int numContacts = 0;
    for (Pidor pidor : pidorsOfChat) {
      long pidorTgId = pidor.getTgId();
      for (Long anotherChatId : chatIds) {
        if (anotherChatId.equals(chatId)) {
          continue;
        }
        boolean hasSameUser =
            pidorRepository.getByChat(chatId).stream()
                .map(Pidor::getTgId)
                .anyMatch(id -> id == pidorTgId);
        if (hasSameUser) {
          numContacts++;
        }
      }
    }
    return new IntText(numContacts);
  }

  private Text getMemberCount(long chatId) {
    return telegramService.getChatMemberCount(chatId).map(IntText::new).orElse(new IntText(-1));
  }

  private Text getChatName(long chatId) {
    return telegramService
        .getChat(chatId)
        .map(Chat::getTitle)
        .map(SimpleText::new)
        .orElse(new SimpleText("-"));
  }

  private Text getDuration(long chatId) {
    return dailyPidorRepository.getByChat(chatId).stream()
        .min(Comparator.comparing(DailyPidor::getLocalDate))
        .map(DailyPidor::getLocalDate)
        .map(this::getDuration)
        .orElse(new SimpleText("-"));
  }

  private Text getDuration(LocalDate firstDate) {
    LocalDate now = DateUtil.now();

    if (firstDate.plusYears(2).isBefore(now)) {
      return new SimpleText(TGUtil.escapeHTML(">2 лет"));
    }
    if (firstDate.plusYears(1).isBefore(now)) {
      return new SimpleText(TGUtil.escapeHTML(">1 года"));
    }
    for (int i = 11; i >= 1; i--) {
      if (firstDate.plusMonths(i).isBefore(now)) {
        return new ParametizedText(TGUtil.escapeHTML("<{0} месяцев"), new IntText(i));
      }
    }
    return new ParametizedText("Создан {0}", new DateText(firstDate));
  }

  @Override
  public StatType getType() {
    return StatType.CHAT_DETAILS;
  }
}
