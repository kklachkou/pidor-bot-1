package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
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
public class PidorRegCommandHandler implements CommandHandler {

  @Autowired private PidorService pidorService;
  @Autowired private BotActionCollector botActionCollector;

  private final Selection<String> alreadyRegMessages;
  private final Selection<String> newRegMessages;

  public PidorRegCommandHandler() {
    alreadyRegMessages =
        new ConsistentSelection<>(
            "Эй, педик, ты уже в игре",
            "Педик, ты уже в игре",
            "Дважды педиком не стать. Ты уже в игре");
    newRegMessages =
        new ConsistentSelection<>(
            "Опа, в банде новый <b>педик</b>. Давайте поприветствуем {0}",
            "Опа, в банде новый <b>педик</b>. Похлопаем по жопке {0}",
            "В банде новый <b>педик</b>. Давайте поприветствуем {0}");
  }

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    int userId = message.getFrom().getId();
    Optional<Pidor> existingPidor = pidorService.getPidor(chatId, userId);
    if (existingPidor.isPresent()) {
      botActionCollector.collectHTMLMessage(chatId, alreadyRegMessages.next());
    } else {
      final Pidor pidor = pidorService.createPidor(chatId, message.getFrom());
      botActionCollector.text(
          chatId, new ParametizedText(newRegMessages.next(), new FullNamePidorText(pidor)));
      botActionCollector.wait(chatId, 1, ChatAction.TYPING);
      botActionCollector.sticker(chatId, StickerType.COOL.getRandom());
    }
  }

  @Override
  public Command getCommand() {
    return Command.PIDOR_REG;
  }
}
