package by.kobyzau.tg.bot.pbot.handlers.command.handler;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.handlers.command.Command;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.program.text.pidor.FullNamePidorText;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.sticker.StickerType;
import by.kobyzau.tg.bot.pbot.util.TGUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Component
public class RegPidorCommandHandler implements CommandHandler {

  @Autowired private PidorService pidorService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private TelegramService telegramService;

  @Override
  public void processCommand(Message message, String text) {
    Message replyToMessage = message.getReplyToMessage();
    if (replyToMessage == null) {
      botActionCollector.text(
          message.getChatId(),
          new SimpleText("Ты должен указать эту команду в ответе на чье-то сообщение"),
          message.getMessageId());
      return;
    }
    User user = replyToMessage.getFrom();
    if (user.getIsBot() != null && user.getIsBot()) {
      botActionCollector.text(
          message.getChatId(),
          new SimpleText("Это сообщение какого-то бота..."),
          message.getMessageId());
      return;
    }
    Optional<ChatMember> chatMember =
        telegramService.getChatMember(message.getChatId(), user.getId());
    if (!TGUtil.isChatMember(chatMember)) {
      botActionCollector.text(
          message.getChatId(),
          new SimpleText("Не вижу его в этом чате..."),
          message.getMessageId());
      return;
    }
    regPidor(message.getChatId(), user);
  }

  public void regPidor(long chatId, User user) {
    int userId = user.getId();
    Optional<Pidor> existingPidor = pidorService.getPidor(chatId, userId);
    if (existingPidor.isPresent()) {
      botActionCollector.text(
          chatId, new RandomText("Этот пидор уже зареган", "Он и так уже в игре"));
    } else {
      final Pidor pidor = pidorService.createPidor(chatId, user);
      botActionCollector.text(
          chatId,
          new ParametizedText(
              new RandomText(
                  "Опа, в банде новый <b>педик</b>. Давайте поприветствуем {0}",
                  "Опа, в банде новый <b>педик</b>. Похлопаем по жопке {0}",
                  "В банде новый <b>педик</b>. Давайте поприветствуем {0}"),
              new FullNamePidorText(pidor)));
      botActionCollector.wait(chatId, 1, ChatAction.TYPING);
      botActionCollector.sticker(chatId, StickerType.LOL.getRandom());
    }
  }

  @Override
  public Command getCommand() {
    return Command.REG_PIDOR;
  }
}
