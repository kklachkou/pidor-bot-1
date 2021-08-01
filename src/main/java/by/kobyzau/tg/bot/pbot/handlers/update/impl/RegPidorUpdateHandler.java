package by.kobyzau.tg.bot.pbot.handlers.update.impl;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.handlers.command.ParsedCommand;
import by.kobyzau.tg.bot.pbot.handlers.command.parser.CommandParser;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandler;
import by.kobyzau.tg.bot.pbot.handlers.update.UpdateHandlerStage;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.printer.UserPrinter;
import by.kobyzau.tg.bot.pbot.program.selection.ConsistentSelection;
import by.kobyzau.tg.bot.pbot.program.selection.Selection;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class RegPidorUpdateHandler implements UpdateHandler {
  @Autowired private PidorService pidorService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private TelegramService telegramService;
  @Autowired private Logger logger;
  private final Selection<String> catchedPidorMessage =
      new ConsistentSelection<>(
          "Ага, попался, {0}\nТеперь ты в игре, хочет ты этого или нет",
          "{0}, зря ты заговорил\nТеперь ты в игре",
          "Добавлю ка я {0} в игру",
          "Теперь {0} участвует в игре");

  @Override
  public UpdateHandlerStage getStage() {
    return UpdateHandlerStage.INVOKE_USER;
  }

  @Override
  public boolean handleUpdate(Update update) {
    if (!update.hasMessage()) {
      return false;
    }

   return updateUser(update.getMessage().getChatId(), update.getMessage().getFrom());
  }

  private boolean updateUser(long chatId, User user) {
    Boolean bot = user.getIsBot();
    if (bot != null && bot) {
      return false;
    }
    Optional<ChatMember> chatMember = telegramService.getChatMember(chatId, user.getId());
    if (!chatMember.isPresent() || !TGUtil.isChatMember(chatMember)) {
      return false;
    }
    Optional<Pidor> existingPidor = pidorService.getPidor(chatId, user.getId());
    if (existingPidor.isPresent()) {
      return false;
    }
    botActionCollector.typing(chatId);
    logger.info(
        "\uD83D\uDC66\uD83C\uDFFB Creating new Pidor "
            + chatMember.get().getStatus()
            + " for chat "
            + chatId
            + ":\n\n"
            + new UserPrinter(user));

    Pidor pidor = pidorService.createPidor(chatId, user);
    botActionCollector.text(
        chatId, new ParametizedText(catchedPidorMessage.next(), new FullNamePidorText(pidor)));
    botActionCollector.wait(chatId, 1, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.LOL.getRandom());
    return true;
  }
}
