package by.kobyzau.tg.bot.pbot.handlers.stat.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.stat.StatHandler;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.model.StatType;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.DateText;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.Text;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.telegraph.TelegraphService;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegraph.api.objects.Node;
import org.telegram.telegraph.api.objects.NodeElement;
import org.telegram.telegraph.api.objects.NodeText;

import static by.kobyzau.tg.bot.pbot.telegraph.TelegraphType.STATISTIC;
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
  private static final LocalDate DEFAULT_DATE = LocalDate.of(2050, 1, 1);

  @Override
  public void printStat(long chatIdToSend) {

    logger.info("#DetailedStat Start");
    List<Long> chatIds =
        pidorRepository.getAll().stream()
            .map(Pidor::getChatId)
            .distinct()
            .collect(Collectors.toList());
    logger.info("#DetailedStat Got chat count");
    List<ChatStat> chatStats = new ArrayList<>();
    for (int i = 0; i < chatIds.size(); i++) {
      Long chatId = chatIds.get(i);
      ChatStat chatStat = toStat(chatId);
      chatStats.add(chatStat);
      if (chatStat.getContacts().values().stream().anyMatch(c -> c.size() > 0)) {
        createContactPage(chatStat, STATISTIC.getLinkedId("CONTACTS", chatStat.chatId));
      }
      logger.info("#DetailedStat " + (i + 1) + "/" + chatIds.size());
    }
    Comparator<ChatStat> countComparator =
        Comparator.comparing(c -> c.getContacts().keySet().size());
    Comparator<ChatStat> contactComparator =
        Comparator.comparing(c -> c.getContacts().values().size());
    createPage(
        chatStats,
        Comparator.comparing(ChatStat::getFirstDate),
        "По времени",
        STATISTIC.getLinkedId("Duration"));
    createPage(
        chatStats,
        countComparator.reversed(),
        "По колличеству пидоров",
        STATISTIC.getLinkedId("Count"));
    createPage(
        chatStats,
        contactComparator.reversed(),
        "По колличеству контактов",
        STATISTIC.getLinkedId("Contact"));
    botActionCollector.text(
        chatIdToSend,
        new ParametizedText(
            "{0}: {1}\n{2}: {3}",
            new SimpleText("По времени"),
            new SimpleText(telegraphService.getPage(STATISTIC.getLinkedId("Duration")).getUrl()),
            new SimpleText("По Контактам"),
            new SimpleText(telegraphService.getPage(STATISTIC.getLinkedId("Contact")).getUrl()),
            new SimpleText("По Колличеству"),
            new SimpleText(telegraphService.getPage(STATISTIC.getLinkedId("Count")).getUrl())));
  }

  private void createPage(
      List<ChatStat> chatStats, Comparator<ChatStat> comparator, String name, String linkedId) {
    List<Node> content = new ArrayList<>();
    content.add(
        new NodeElement(
            "p",
            emptyMap(),
            Collections.singletonList(new NodeText(chatStats.size() + " чатов."))));

    List<ChatStat> sorted = chatStats.stream().sorted(comparator).collect(Collectors.toList());
    for (ChatStat chatStat : sorted) {
      boolean hasContacts = chatStat.getContacts().values().stream().anyMatch(c -> c.size() > 0);
      content.add(
          new NodeElement(
              "p",
              emptyMap(),
              Arrays.asList(
                  new NodeElement("br", emptyMap(), emptyList()),
                  new NodeElement(
                      "b", emptyMap(), singletonList(new NodeText("Чат " + chatStat.getChatId()))),
                  new NodeElement("br", emptyMap(), emptyList()),
                  new NodeElement("b", emptyMap(), singletonList(new NodeText("Имя: "))),
                  new NodeText(chatStat.name),
                  new NodeElement("br", emptyMap(), emptyList()),
                  new NodeElement(
                      "b", emptyMap(), singletonList(new NodeText("Колличество людей: "))),
                  new NodeText(String.valueOf(chatStat.peopleCount)),
                  new NodeElement("br", emptyMap(), emptyList()),
                  new NodeElement(
                      "b", emptyMap(), singletonList(new NodeText("Колличество пидоров: "))),
                  new NodeText(String.valueOf(chatStat.getContacts().keySet().size())),
                  new NodeElement("br", emptyMap(), emptyList()),
                  new NodeElement(
                      "b", emptyMap(), singletonList(new NodeText("Колличество игр: "))),
                  new NodeText(String.valueOf(chatStat.getGameCount())),
                  new NodeElement("br", emptyMap(), emptyList()),
                  new NodeElement("b", emptyMap(), singletonList(new NodeText("Срок: "))),
                  new NodeText(getDuration(chatStat.getFirstDate()).text()),
                  new NodeElement("br", emptyMap(), emptyList()),
                  new NodeElement(
                      "b", emptyMap(), singletonList(new NodeText("Людей-контактов: "))),
                  hasContacts
                      ? new NodeElement(
                          "a",
                          Collections.singletonMap(
                              "href",
                              telegraphService.getPage("Stat-Contact-" + chatStat.chatId).getUrl()),
                          Collections.singletonList(
                              new NodeText(
                                  String.valueOf(
                                      chatStat.getContacts().values().stream()
                                          .filter(c -> c.size() > 0)
                                          .count()))))
                      : new NodeText("0"),
                  new NodeElement("br", emptyMap(), emptyList()))));
    }

    telegraphService.createPageIfNotExist(linkedId);
    telegraphService.updatePage(linkedId, name, content);
  }

  private void createContactPage(ChatStat chatStat, String linkedId) {
    List<Node> content = new ArrayList<>();
    List<Map.Entry<Long, Set<Long>>> entries =
        chatStat.getContacts().entrySet().stream()
            .filter(e -> e.getValue().size() > 0)
            .collect(Collectors.toList());
    content.add(new NodeText("Списки контактов"));
    for (Map.Entry<Long, Set<Long>> entry : entries) {
      Long pidorId = entry.getKey();
      Set<Long> linkedChats = entry.getValue();
      String name =
          pidorRepository
              .getByChatAndPlayerTgId(chatStat.getChatId(), pidorId)
              .map(Pidor::getFullName)
              .orElse("-");
      content.add(new NodeElement("br", emptyMap(), emptyList()));
      content.add(
          new NodeText(
              name
                  + ": "
                  + linkedChats.stream().map(this::getChatName).collect(Collectors.joining(", "))));
    }
    logger.debug("Contact Content for " + linkedId + " is " + content);
    telegraphService.createPageIfNotExist(linkedId);
    try {
      telegraphService.updatePage(
          linkedId,
          "Контакты для чата " + chatStat.chatId,
          singletonList(new NodeElement("p", emptyMap(), content)));
    } catch (Exception e) {
      logger.error("Cannot create contact page for chat " + chatStat.chatId, e);
    }
  }

  private ChatStat toStat(long chatId) {
    return new ChatStat(
        chatId,
        getContacts(chatId),
        getChatName(chatId),
        getMemberCount(chatId),
        getNumGames(chatId),
        getFirstDate(chatId),
        getEndDate(chatId));
  }

  private Map<Long, Set<Long>> getContacts(long chatId) {
    List<Pidor> pidorsOfChat = pidorRepository.getByChat(chatId);
    List<Long> chatIds =
        pidorRepository.getAll().stream()
            .map(Pidor::getChatId)
            .distinct()
            .collect(Collectors.toList());
    Map<Long, Set<Long>> contacts = new HashMap<>();
    for (Pidor pidor : pidorsOfChat) {
      long pidorTgId = pidor.getTgId();
      contacts.put(pidorTgId, new HashSet<>());
      for (Long anotherChatId : chatIds) {
        if (anotherChatId.equals(chatId)) {
          continue;
        }
        boolean hasSameUser =
            pidorRepository.getByChat(anotherChatId).stream()
                .map(Pidor::getTgId)
                .anyMatch(id -> id == pidorTgId);
        if (hasSameUser) {
          Set<Long> anotherContacts = contacts.get(pidorTgId);
          anotherContacts.add(anotherChatId);
          contacts.put(pidorTgId, anotherContacts);
        }
      }
    }
    return contacts;
  }

  private int getMemberCount(long chatId) {
    return telegramService.getChatMemberCount(chatId).orElse(-1);
  }

  private String getChatName(long chatId) {
    return telegramService.getChat(chatId).map(Chat::getTitle).orElse("-");
  }

  private int getNumGames(long chatId) {
    return dailyPidorRepository.getByChat(chatId).size();
  }

  private LocalDate getFirstDate(long chatId) {
    return dailyPidorRepository.getByChat(chatId).stream()
        .min(Comparator.comparing(DailyPidor::getLocalDate))
        .map(DailyPidor::getLocalDate)
        .orElse(DEFAULT_DATE);
  }

  private LocalDate getEndDate(long chatId) {
    return dailyPidorRepository.getByChat(chatId).stream()
        .max(Comparator.comparing(DailyPidor::getLocalDate))
        .map(DailyPidor::getLocalDate)
        .orElse(DEFAULT_DATE);
  }

  private Text getDuration(LocalDate firstDate) {
    LocalDate now = DateUtil.now();

    if (firstDate.plusYears(2).isBefore(now)) {
      return new SimpleText(">2 лет");
    }
    if (firstDate.plusYears(1).isBefore(now)) {
      return new SimpleText(">1 года");
    }
    for (int i = 11; i >= 1; i--) {
      if (firstDate.plusMonths(i).isBefore(now)) {
        return new ParametizedText("<{0} месяцев", new IntText(i));
      }
    }
    for (int i = 31; i >= 1; i--) {
      if (firstDate.plusDays(i).isBefore(now)) {
        return new ParametizedText("<{0} дней", new IntText(i));
      }
    }
    return new ParametizedText("Создан {0}", new DateText(firstDate));
  }

  private static class ChatStat {
    private final long chatId;
    private final Map<Long, Set<Long>> contacts;
    private final String name;
    private final int peopleCount;
    private final int gameCount;
    private final LocalDate firstDate;
    private final LocalDate lastDate;

    public ChatStat(
        long chatId,
        Map<Long, Set<Long>> contacts,
        String name,
        int peopleCount,
        int gameCount,
        LocalDate firstDate,
        LocalDate lastDate) {
      this.chatId = chatId;
      this.contacts = contacts;
      this.name = name;
      this.peopleCount = peopleCount;
      this.gameCount = gameCount;
      this.firstDate = firstDate;
      this.lastDate = lastDate;
    }

    public long getChatId() {
      return chatId;
    }

    public Map<Long, Set<Long>> getContacts() {
      return contacts;
    }

    public String getName() {
      return name;
    }

    public int getPeopleCount() {
      return peopleCount;
    }

    public LocalDate getFirstDate() {
      return firstDate;
    }

    public LocalDate getLastDate() {
      return lastDate;
    }

    public int getGameCount() {
      return gameCount;
    }
  }

  @Override
  public StatType getType() {
    return StatType.CHAT_DETAILS;
  }
}
