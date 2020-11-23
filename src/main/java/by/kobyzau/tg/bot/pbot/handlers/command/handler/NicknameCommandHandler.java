package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@Component
public class NicknameCommandHandler implements CommandHandler {

  @Autowired private PidorService pidorService;

  @Autowired private BotActionCollector botActionCollector;

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    Optional<Pidor> pidor = pidorService.getPidor(chatId, message.getFrom().getId());
    if (!pidor.isPresent()) {
      botActionCollector.collectHTMLMessage(
          chatId, "Сначала зарегайся в игре: /" + Command.PIDOR_REG.getName());
      return;
    }
    if (StringUtil.isBlank(text)) {
      botActionCollector.collectHTMLMessage(
          chatId,
          "Вы не прислали псевдоним.\n\nПример ввода:\n/nickname Хороший пидор\n\nЧтобы удалить введите <b>CLEAR</b>  в качестве никнейма");
      return;
    }
    Pidor pidorInstance = pidor.get();
    if ("CLEAR".equalsIgnoreCase(text.trim())) {

      pidorInstance.setNickname(null);
      pidorService.updatePidor(pidorInstance);
      botActionCollector.collectHTMLMessage(chatId, "Псевдоним удалён");
      return;
    }
    pidorInstance.setNickname(text);
    pidorService.updatePidor(pidorInstance);
    botActionCollector.collectHTMLMessage(chatId, "У тебя теперь есть никнейм");
    botActionCollector.wait( chatId, ChatAction.TYPING);
    botActionCollector.sticker(chatId, StickerType.COOL.getRandom());
  }

  @Override
  public Command getCommand() {
    return Command.NICKNAME;
  }
}
