package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.repository.dailypidor.DailyPidorRepository;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.CollectionUtil;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MyStatusCommandHandler implements CommandHandler {

  @Autowired private DailyPidorRepository dailyPidorRepository;

  @Autowired private PidorService pidorService;

  @Autowired private BotActionCollector botActionCollector;

  private final Selection<String> noDataMessages;
  private final Selection<String> welcomeMessage;

  public MyStatusCommandHandler() {
    this.noDataMessages =
        new ConsistentSelection<>(
            "На тебя у меня нет данных",
            "Ты чисто перед Всевышним",
            "Ты - натурал",
            "Пусто",
            "Нет данных",
            "Ничего о тебе не могу сказать");
    this.welcomeMessage =
        new ConsistentSelection<>(
            "Вот вся информация о тебе",
            "Давайте поглядим...",
            "Так, сейчас узнаем всё о тебе",
            "Расчитываю...");
  }

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    long userId = message.getFrom().getId();
    List<DailyPidor> dailyPidors =
        dailyPidorRepository.getByChat(chatId).stream()
            .filter(d -> d.getPlayerTgId() == userId)
            .sorted(Comparator.comparing(DailyPidor::getLocalDate).reversed())
            .collect(Collectors.toList());

    if (CollectionUtil.isEmpty(dailyPidors)) {
      botActionCollector.collectHTMLMessage(chatId, noDataMessages.next());
      botActionCollector.sticker(chatId, StickerType.SAD.getRandom());
      return;
    }
    botActionCollector.collectHTMLMessage(chatId, welcomeMessage.next());

    botActionCollector.wait( chatId, 1, ChatAction.TYPING);
    writeAboutThisYear(chatId, dailyPidors);

    botActionCollector.wait( chatId, 1,  ChatAction.TYPING);
    writeAboutLastYear(chatId, dailyPidors);

    botActionCollector.wait( chatId, 1, ChatAction.TYPING);
    writeAboutAll(chatId, dailyPidors);

    botActionCollector.wait( chatId, 1, ChatAction.TYPING);
    writeAboutLastTime(chatId, dailyPidors);
    Optional<Pidor> pidor = pidorService.getPidor(chatId, userId);
    if (pidor.isPresent()) {
      Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidor.get().getSticker());
      if (pidorSticker.isPresent()) {
        botActionCollector.wait(chatId, ChatAction.TYPING);
        botActionCollector.sticker(chatId, pidorSticker.get().getRandom());
      }
    }
  }

  private void writeAboutThisYear(long chatId, List<DailyPidor> dailyPidors) {
    int currentYear = DateUtil.now().getYear();
    long count =
        dailyPidors.stream()
            .filter(d -> d.getLocalDate() != null)
            .filter(d -> d.getLocalDate().getYear() == currentYear)
            .count();
    if (count > 0) {
      botActionCollector.collectHTMLMessage(
          chatId, "В этом году вы были пидором " + count + " раз");
    } else {
      botActionCollector.collectHTMLMessage(chatId, "В этом году вы еще ниразу не были пидором");
    }
  }

  private void writeAboutLastYear(long chatId, List<DailyPidor> dailyPidors) {
    int prevYear = DateUtil.now().getYear() - 1;
    long count =
        dailyPidors.stream()
            .filter(d -> d.getLocalDate() != null)
            .filter(d -> d.getLocalDate().getYear() == prevYear)
            .count();
    if (count > 0) {
      botActionCollector.collectHTMLMessage(
          chatId, "В прошлом году вы были пидором " + count + " раз");
    } else {
      botActionCollector.collectHTMLMessage(chatId, "В прошлом году вы ниразу не были пидором");
    }
  }

  private void writeAboutAll(long chatId, List<DailyPidor> dailyPidors) {
    int count = dailyPidors.size();
    if (count > 0) {
      botActionCollector.collectHTMLMessage(chatId, "В целом вы были пидором " + count + " раз");
    } else {
      botActionCollector.collectHTMLMessage(chatId, "В целом вы никогда не были пидором");
    }
  }

  private void writeAboutLastTime(long chatId, List<DailyPidor> dailyPidors) {
    if (CollectionUtil.isNotEmpty(dailyPidors)) {
      LocalDate pidorDate = dailyPidors.get(0).getLocalDate();

      if (pidorDate != null) {
        LocalDate now = DateUtil.now();
        long days = now.toEpochDay() - pidorDate.toEpochDay();
        if (days == 0) {
          botActionCollector.collectHTMLMessage(chatId, "Последний раз вы были пидором сегодня:)");
          botActionCollector.sticker(chatId, StickerType.LOL.getRandom());
        } else {
          botActionCollector.collectHTMLMessage(
              chatId,
              "Последний раз вы были пидором " + days + " " + getDaysWord((int) days) + " назад");
        }
      }
    }
  }

  private String getDaysWord(int days) {
    switch (days) {
      case 1:
        return "день";
      case 2:
      case 3:
      case 4:
        return "дня";
      default:
        return "дней";
    }
  }

  @Override
  public Command getCommand() {
    return Command.MY_STATUS;
  }
}
