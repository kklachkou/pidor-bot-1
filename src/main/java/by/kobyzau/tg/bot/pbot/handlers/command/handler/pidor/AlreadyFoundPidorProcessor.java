package by.kobyzau.tg.bot.pbot.handlers.command.handler.pidor;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.model.DailyPidor;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@Component
public class AlreadyFoundPidorProcessor {

  @Autowired private PidorService pidorService;

  @Autowired private BotActionCollector botActionCollector;

  private final Selection<String> pidorAlreadyFoundMessage;
  private final Selection<String> pidorEscapeMessage;

  public AlreadyFoundPidorProcessor(
      Selection<String> pidorAlreadyFoundMessage, Selection<String> pidorEscapeMessage) {
    this.pidorAlreadyFoundMessage = pidorAlreadyFoundMessage;
    this.pidorEscapeMessage = pidorEscapeMessage;
  }

  public AlreadyFoundPidorProcessor() {
    this(
        new ConsistentSelection<>(
            "Сегодня у нас уже есть пидор дня.\nВеликий и ужасный {0}",
            "Пидора сегодня уже нашли.\nЭто {0}"),
        new ConsistentSelection<>(
            "Пидор был, но он сбежал. Очко ему всё равно начистию",
            "Пидор был, но он сбежал. Вот Гад! Очко ему всё равно начистию"));
  }

  public void processAlreadyFound(Message message, DailyPidor dailyPidor) {
    long chatId = message.getChatId();
    Optional<Pidor> pidor =
        pidorService.getPidor(dailyPidor.getChatId(), dailyPidor.getPlayerTgId());
    if (pidor.isPresent()) {
      botActionCollector.text(
          chatId,
          new ParametizedText(pidorAlreadyFoundMessage.next(), new FullNamePidorText(pidor.get())));

      Optional<StickerType> pidorSticker = StickerType.getPidorSticker(pidor.get().getSticker());
      if (pidorSticker.isPresent()) {
        botActionCollector.wait(chatId, ChatAction.TYPING);
        botActionCollector.sticker(chatId, pidorSticker.get().getRandom());
      }

    } else {
      botActionCollector.collectHTMLMessage(chatId, pidorEscapeMessage.next());
    }
  }
}
