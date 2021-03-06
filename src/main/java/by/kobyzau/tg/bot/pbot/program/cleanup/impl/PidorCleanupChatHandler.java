package by.kobyzau.tg.bot.pbot.program.cleanup.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.cleanup.CleanupHandler;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNameLinkedPidorText;
import by.kobyzau.tg.bot.pbot.repository.pidor.PidorRepository;
import by.kobyzau.tg.bot.pbot.service.DateService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PidorCleanupChatHandler implements CleanupHandler {

  public static final int NOTIFY_DAYS = 15;
  public static final int CLEAR_DAYS = 16;
  @Autowired private Logger logger;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private PidorRepository pidorRepository;
  @Autowired private PidorService pidorService;
  @Autowired private DateService dateService;
  @Autowired private TelegramService telegramService;

  @Override
  public void cleanup() {
    List<Long> chatIds =
        pidorRepository.getChatIdsWithPidors().stream().distinct().collect(Collectors.toList());
    for (long chatId : chatIds) {
      cleanEmptyChats(chatId);
      pidorRepository.getByChat(chatId).forEach(this::notifyPidor);
      pidorRepository.getByChat(chatId).forEach(this::handlePidor);
    }
  }

  private void cleanEmptyChats(long chatId) {
    int activeNum = pidorService.getByChat(chatId).size();
    if (activeNum == 0) {
      String chatName =
          telegramService
              .getChat(chatId)
              .map(c -> chatId + ": " + c.getTitle())
              .orElse(String.valueOf(chatId));
      logger.warn(new ParametizedText("Deleting empty chat {0}", new SimpleText(chatName)).text());
      pidorRepository.getByChat(chatId).stream().map(Pidor::getId).forEach(pidorRepository::delete);
    }
  }

  private void notifyPidor(Pidor pidor) {
    long chatId = pidor.getChatId();
    LocalDate startDate = dateService.getNow().minusDays(NOTIFY_DAYS);
    if (startDate.isEqual(pidor.getUsernameLastUpdated())) {
      logger.debug(
          new ParametizedText(
                  "Notify inactive pidor {0} from chat {1}",
                  new FullNamePidorText(pidor), new SimpleText(String.valueOf(chatId)))
              .text());
      botActionCollector.wait(pidor.getChatId(), ChatAction.TYPING);
      botActionCollector.text(
          pidor.getChatId(),
          new ParametizedText(
              new RandomText(
                  "{0} - ???????? ???? ???????????? ?????? ?????? ??????????????, ?? ???????? ?????????????? ???? ????????",
                  "{0} - ???????? ??????-???????????? ???????? ?? ???????? ????????, ?????????? ???????????? ?? ????????",
                  "{0} - ?????? ????????????????, ???????? ?????? ???????? ?? ???????? ????????...???????? ???????????? ???????????????????? ??????????????, ?? ?? ?????????? ???? ???????? ????????????",
                  "{0} - ???? ??????????????, ?????????????? ??????????, ?????????? ?? ???????? ?????????? ???? ????????",
                  "{0} - ?????????? ?????????????? ???? ???????????????? ???????? ???? ????????????, ?????????? ?? ???????? ???????? ?? ??????????"),
              new ShortNameLinkedPidorText(pidor)));
    }
  }

  private void handlePidor(Pidor pidor) {
    long chatId = pidor.getChatId();
    LocalDate startDate = dateService.getNow().minusDays(CLEAR_DAYS);
    if (startDate.isAfter(pidor.getUsernameLastUpdated())) {
      String chatName =
          telegramService
              .getChat(chatId)
              .map(c -> chatId + ": " + c.getTitle())
              .orElse(String.valueOf(chatId));
      logger.warn(
          new ParametizedText(
                  "Deleting inactive pidor {0} from chat {1}",
                  new FullNamePidorText(pidor), new SimpleText(chatName))
              .text());
      pidorRepository.delete(pidor.getId());
    }
  }
}
