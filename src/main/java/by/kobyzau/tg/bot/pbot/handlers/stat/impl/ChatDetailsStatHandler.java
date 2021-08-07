package by.kobyzau.tg.bot.pbot.handlers.stat.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.stat.StatHandler;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.model.TelegraphPage;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.BoldText;
import by.kobyzau.tg.bot.pbot.program.text.DateText;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.NewLineText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.program.text.TextBuilder;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.telegraph.TelegraphService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegraph.api.objects.Node;
import org.telegram.telegraph.api.objects.NodeElement;
import org.telegram.telegraph.api.objects.NodeText;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;

@Component
public class ChatDetailsStatHandler implements StatHandler {
  @Autowired private TelegramService telegramService;
  @Autowired private DailyPidorRepository dailyPidorRepository;
  @Autowired private PidorRepository pidorRepository;
  @Autowired private Logger logger;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private TelegraphService telegraphService;

  @Override
  public void printStat(long chatIdToSend) {

    logger.info("#DetailedStat Start");
    List<Long> chatIds =
        pidorRepository.getAll().stream()
            .map(Pidor::getChatId)
            .distinct()
            .collect(Collectors.toList());
    List<Node> content = new ArrayList<>();
    content.add(
        new NodeElement(
            "p", emptyMap(), Collections.singletonList(new NodeText(chatIds.size() + " чатов."))));

    logger.info("#DetailedStat Got chat count");
    for (int i = 0; i < chatIds.size(); i++) {

      logger.info("#DetailedStat " + (i + 1) + "/" + chatIds.size());
      Long chatId = chatIds.get(i);

      try {
        content.add(
            new NodeElement(
                "p",
                emptyMap(),
                Arrays.asList(
                    new NodeElement("br", emptyMap(), emptyList()),
                    new NodeElement("b", emptyMap(), singletonList(new NodeText("Чат " + chatId))),
                    new NodeElement("br", emptyMap(), emptyList()),
                    new NodeElement("b", emptyMap(), singletonList(new NodeText("Имя: "))),
                    new NodeText(getChatName(chatId).text()),
                    new NodeElement("br", emptyMap(), emptyList()),
                    new NodeElement(
                        "b", emptyMap(), singletonList(new NodeText("Колличество людей: "))),
                    new NodeText(getMemberCount(chatId).text()),
                    new NodeElement("br", emptyMap(), emptyList()),
                    new NodeElement(
                        "b", emptyMap(), singletonList(new NodeText("Колличество пидоров: "))),
                    new NodeText(getPidorCount(chatId).text()),
                    new NodeElement("br", emptyMap(), emptyList()),
                    new NodeElement("b", emptyMap(), singletonList(new NodeText("Срок: "))),
                    new NodeText(getDuration(chatId).text()),
                    new NodeElement("br", emptyMap(), emptyList()),
                    new NodeElement(
                        "b", emptyMap(), singletonList(new NodeText("Людей-контактов: "))),
                    new NodeText(getContactPeopleNum(chatId).text()),
                    new NodeElement("br", emptyMap(), emptyList()))));

      } catch (Exception e) {
        logger.error("Cannot get Stat for chat " + chatId, e);
      }
    }
    logger.info("#DetailedStat Completed");
    telegraphService.createPageIfNotExist("TEMP");
    telegraphService.updatePage("TEMP", "Stat", content);
    TelegraphPage telegraphPage = telegraphService.getPage("TEMP");
    botActionCollector.text(chatIdToSend, new SimpleText(telegraphPage.getUrl()));
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
