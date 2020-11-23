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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;

@Component
public class RegPidorCommandHandler implements CommandHandler {

  @Value("${bot.username}")
  private String botUserName;

  @Autowired private PidorService pidorService;
  @Autowired private BotActionCollector botActionCollector;
  @Autowired private TelegramService telegramService;

  @Override
  public void processCommand(Message message, String text) {
    long chatId = message.getChatId();
    Optional<User> user = getUser(message);
    if (!user.isPresent()) {
      botActionCollector.text(
          chatId,
          new ParametizedText(
              "Ты не указал цель или я не могу найти этого пользователя.\n"
                  + " Укажи юзера через @ сразу после комманды. Пример:\n /{0} @{1}",
              new SimpleText(Command.REG_PIDOR.getName()), new SimpleText(botUserName)),
          message.getMessageId());
      return;
    }
    if (user.get().getIsBot() != null && user.get().getIsBot()) {
      botActionCollector.text(
          chatId, new SimpleText("Это сообщение какого-то бота..."), message.getMessageId());
      return;
    }
    Optional<ChatMember> chatMember = telegramService.getChatMember(chatId, user.get().getId());
    if (!TGUtil.isChatMember(chatMember)) {
      botActionCollector.text(
          chatId, new SimpleText("Не вижу его в этом чате..."), message.getMessageId());
      return;
    }
    regPidor(chatId, user.get());
  }

  private Optional<User> getUser(Message message) {
    List<MessageEntity> entities = message.getEntities();
    if (entities != null) {
      for (MessageEntity entity : entities) {
        if (entity.getUser() != null) {
          return Optional.of(entity.getUser());
        }
      }
    }
    return Optional.empty();
  }

  private void regPidor(long chatId, User user) {
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
